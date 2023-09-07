package com.lee.control.db;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.RandomUtil;
import com.jfoenix.controls.JFXToggleButton;
import com.lee.database.std.entity.*;
import com.lee.database.std.mapper.BoxliftStdMapper;
import com.lee.database.std.service.impl.*;
import com.lee.util.*;
import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLController;
import de.felixroske.jfxsupport.FXMLView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Date: 2023/6/4 20:39
 * @Description: TODO
 */
@FXMLController
@FXMLView(value = "/fxml/db/dbStd.fxml")
@ConditionalOnProperty(name = "project.name", havingValue = "YiQi")
public class MainStdDbController extends AbstractFxmlView implements Initializable {
    private static final Logger log = LoggerFactory.getLogger(MainStdDbController.class);

    @FXML
    protected JFXToggleButton autoInbound, autoOutbound, autoMove;

    @Autowired
    protected BoxliftStdMapper boxliftStdMapper;

    @Autowired
    protected StdBoxliftshelfpdServiceImpl boxliftshelfpdService;

    @Autowired
    protected StdTaskServiceImpl taskService;

    @Autowired
    protected StdLocationsServiceImpl locationsService;

    @Autowired
    protected StdAnnounceServiceImpl announceService;


    public volatile int outBoxId = 1;
    public volatile int moveBoxId = 1;

    public volatile int announceId = 1;

    List<Integer> levels = new ArrayList<>();

    List<Integer> aisles = new ArrayList<>();
    List<Boxlift> lifts = new ArrayList<>();

    @Value("${strategy.inbound.machine}")
    public String inboundMachine;

    @Value("${strategy.inbound.isAppoint}")
    public boolean isAppoint;

    @Value("${strategy.inbound.intervalTime}")
    public Long inboundIntervalTime;

    @Value("${strategy.inbound.exitArea}")
    public boolean inboundExitArea;

    public void initialize(URL location, ResourceBundle resources) {
        ThreadUtil.execute(this::init);
        boxliftStdMapper.selectAll().forEach(boxlift -> {
            lifts.add(boxlift);
            ThreadUtil.createThread("boxLift" + boxlift.getId(), () -> inboundTask(boxlift)).start();
        });

    }

    public void init() {
        outBoxId = this.taskService.initId(2);
        moveBoxId = this.taskService.initId(15);
        announceId = announceService.count() + 1;
        this.levels = this.locationsService.allLevels();
        this.aisles = this.locationsService.allAisles();
        log.info("levels: {}", ArrayUtil.toString(this.levels));
        log.info("aisles: {}", ArrayUtil.toString(this.aisles));
    }

    public <T> T randomFromList(List<T> list) {
        if (list.isEmpty())
            return null;
        return list.get(RandomUtil.randomInt(list.size()));
    }

    @Scheduled(fixedRate = 1000, initialDelay = 1000 * 3)
    public void createAnnounce() {
        try {
            if (!autoInbound.isSelected()) {
                return;
            }
            List<Announce> announceList = new ArrayList<>();
            for (Boxlift lift : lifts) {
                int liftId = lift.getId();
                int aisle = lift.getPos() / 1000;
                List<Announce> annList = announceService.isExitAnnounce(liftId);
                if (annList.size() <= 5) {//为了能换层
                    for (Integer level : levels) {
                        String boxNumber = Constance.BOX_PREFIX + liftId + "-" + announceId;
                        List<Locations> locations = locationsService.inboundLocations(level, aisle);
                        if (locations.size() > 0) {
                            Locations inbound = randomFromList(locations);
                            int location = inbound.getLocation();
                            announceList.add(new Announce(boxNumber, level, location, inbound.getArea()));
                            announceId++;
                        } else {
                            log.info("level:{} 无可用入库货位", level);
                        }
                    }
                }
            }
            if (announceList.size() > 0) {
                boolean b = announceService.saveBatch(announceList);
                announceList.forEach(System.out::println);
                log.info("生成appointAnnounce size:{} {}", announceList.size(), b ? "成功" : "失败");
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }

    public void inboundTask(Boxlift boxlift) {
        int liftId = boxlift.getId();
        int liftPos = boxlift.getPos();
        String deviceIp = boxlift.getIp();
        int annId = this.announceService.initId();
        log.info("BoxLift-[{}], pos: {}, annId: {}, ip: {} start...", liftId, liftPos, annId, deviceIp);
        while (true) {
            try {

                if (!this.autoInbound.isSelected()) {
                    ThreadUtil.sleep(1000L);
                    continue;
                }

                List<Boxliftshelfpd> shelfPds = boxliftshelfpdService.selectPds(liftPos);
                for (Boxliftshelfpd shelfPd : shelfPds) {
                    if (shelfPd.getInboundRequest() == 0) {
                        int level = shelfPd.getLevel();
                        //找对应的预约任务发送请求
                        List<Announce> announces = announceService.selectByIdAndLevel(liftId, level);
                        if (announces.size() > 0) {
                            Announce announce = announces.get(0);
                            String boxId = announce.getBoxNumber();
                            String requestLift = DeviceHttpRequest.sendRequest(deviceIp, 1, liftId, 1, boxId);
                            log.info("boxLift-[{}], request , boxNumber:{} {}", liftId, boxId, requestLift);
                            if (requestLift == null) {
                                log.info("boxLift-[{}],  deviceIp: {} 请求设备地址无效", liftId, deviceIp);
                                ThreadUtil.sleep(1000);
                                continue;
                            }
                            if (!requestLift.equalsIgnoreCase("success")) {
                                log.info("boxLift-[{}],  request {} failed", liftId, requestLift);
                                continue; //跳过，下次再来
                            }
                            ThreadUtil.sleep(2000);//上去耗时

                            //Announce exitAnnounce = announceService.isExitAnnounce(boxId);
                            //while (exitAnnounce == null) {
                            //    ThreadUtil.sleep(2000);
                            //    log.info("lift-[{}] level:{} barcode:{} 已发送请求,等待Announce生成任务...", liftId, level, boxId);
                            //    exitAnnounce = announceService.isExitAnnounce(boxId);
                            //}

                            String requestPD = DeviceHttpRequest.sendRequest(deviceIp, 2, shelfPd.getId(), 1, boxId);
                            log.info("PD-[{}], request boxNumber:{} {}", shelfPd.getId(), boxId, requestPD);
                            ThreadUtil.sleep(2000);//下来耗时
                        }
                    }
                }
            } catch (Exception e) {
                log.info(e.getMessage());
            }
        }
    }

    @Scheduled(fixedRateString = "${strategy.outbound.intervalTime}", initialDelay = 1000 * 3)
    public void outboundTask() {
        try {
            if (!autoOutbound.isSelected()) {
                return;
            }
            List<Task> totalTaskList = taskService.outTask();
            if (totalTaskList.size() > 10) { //为了能换层出库
                return;
            }
            List<Locations> allOut = locationsService.outLocations();
            List<Task> taskList = new ArrayList<>();
            levels.forEach(level -> {
                aisles.forEach(aisle -> {
                    if (!autoOutbound.isSelected()) {
                        return;
                    }
                    List<Locations> locations = allOut.stream().filter(out -> out.getLevel() == level && out.getAisle() == aisle).collect(Collectors.toList());
                    if (locations.size() == 0) {
                        log.info("level:{} aisle:{} 无出库货位可用", level, aisle);
                        return;
                    }

                    for (int i = 1; i <= 3; i++) {
                        Locations location = CommonUtil.randomFromList(locations);
                        if (location == null) {
                            continue;
                        }
                        String boxId = Constance.OUT_BOX_PREFIX + outBoxId;
                        location.setBoxNumber(boxId);
                        taskList.add(STDTaskTemplate.outboundTask(location));
                        locations.remove(location);
                        outBoxId++;
                    }
                    ThreadUtil.sleep(500);
                });

            });
            if (taskList.size() > 0) {
                taskService.saveBatch(taskList);
                taskList.forEach(System.out::println);
                log.info("生成出库任务 size:{}", taskList.size());
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }

    @Scheduled(fixedRateString = "${strategy.move.intervalTime}", initialDelay = 1000 * 3)
    public void moveTask() {
        try {
            if (!autoMove.isSelected()) {
                return;
            }

            List<Task> taskList = new ArrayList<>();
            List<Locations> locationsList = locationsService.list();
            levels.forEach(level -> {
                List<Task> tasks = taskService.moveLibrary(level);
                if (tasks.size() == 0) {
                    aisles.forEach(aisle -> {
                        if (!autoMove.isSelected()) {
                            return;
                        }
                        List<Locations> locations =
                                locationsList.stream().filter(lt ->
                                        lt.getLevel() == level && lt.getAisle() == aisle && lt.getType() == 0).collect(Collectors.toList());
                        for (int i = 0; i < 3; i++) {
                            Locations start = CommonUtil.randomFromList(locations);
                            locations.remove(start);
                            Locations end = CommonUtil.randomFromList(locations);
                            if (start == null || end == null) {
                                continue;
                            }
                            start.setBoxNumber(Constance.MOVE_BOX_PREFIX + moveBoxId);
                            taskList.add(STDTaskTemplate.moveTask(start, end));
                            moveBoxId++;
                        }

                    });
                }


            });
            if (taskList.size() > 0) {
                taskService.saveBatch(taskList);
                taskList.forEach(System.out::println);
                log.info("生成移库任务 size:{}", taskList.size());
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }
}