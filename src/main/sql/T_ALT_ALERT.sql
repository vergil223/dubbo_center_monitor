CREATE TABLE `T_ALT_ALERT` (
  `ID_` bigint(20) NOT NULL AUTO_INCREMENT,
  `TARGET` varchar(255) DEFAULT NULL,
  `TARGET_EXCLUDE` varchar(255) DEFAULT NULL,
  `CONDITION_IDS` varchar(255) DEFAULT NULL,
  `CONDITION_PARAM` varchar(255) DEFAULT NULL,
  `ACTION_IDS` varchar(255) DEFAULT NULL,
  `ACTION_PARAM` varchar(255) DEFAULT NULL,
  `NAME` varchar(255) DEFAULT NULL,
  `DESCRIPTION` varchar(255) DEFAULT NULL,
  `ENABLED` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID_`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;



INSERT INTO `T_ALT_ALERT` (`ID_`, `TARGET`, `TARGET_EXCLUDE`, `CONDITION_IDS`, `CONDITION_PARAM`, `ACTION_IDS`, `ACTION_PARAM`, `NAME`, `DESCRIPTION`, `ENABLED`) VALUES (1, '.*', NULL, '1', 'percentIncrease=4,minutesToCompare=10,thresholdSuccessTimes=100', '2', NULL, 'dubbo method success times with several minutes ago', '监控success times和几分钟前比的冲高', 'Y');
INSERT INTO `T_ALT_ALERT` (`ID_`, `TARGET`, `TARGET_EXCLUDE`, `CONDITION_IDS`, `CONDITION_PARAM`, `ACTION_IDS`, `ACTION_PARAM`, `NAME`, `DESCRIPTION`, `ENABLED`) VALUES (2, '.*', NULL, '2', 'percentIncrease=4,daysToCompare=1,minScope=-5,thresholdSuccessTimes=100', '2', NULL, 'dubbo method success times with several days ago', '监控success times和几天前相同时间段的冲高', 'Y');
INSERT INTO `T_ALT_ALERT` (`ID_`, `TARGET`, `TARGET_EXCLUDE`, `CONDITION_IDS`, `CONDITION_PARAM`, `ACTION_IDS`, `ACTION_PARAM`, `NAME`, `DESCRIPTION`, `ENABLED`) VALUES (3, '.*', NULL, '3', 'thresholdSuccessTimes=10000,minuteScope=10', '2', NULL, 'dubbo method success times exceeds threshold', '监控method success times是否超过阈值', 'N');
INSERT INTO `T_ALT_ALERT` (`ID_`, `TARGET`, `TARGET_EXCLUDE`, `CONDITION_IDS`, `CONDITION_PARAM`, `ACTION_IDS`, `ACTION_PARAM`, `NAME`, `DESCRIPTION`, `ENABLED`) VALUES (4, 'com.lvmama.comm.bee.service.sync.SyncBaseService.countSyncTemplateByCondition', NULL, '3', 'thresholdSuccessTimes=10,minuteScope=5', '3,2', 'mock=return 100,recoverMockSeconds=20', '如果一分钟调用次数超过上线就改成dubbo mock方式', '如果一分钟调用次数超过上线就改成dubbo mock方式', 'N');
INSERT INTO `T_ALT_ALERT` (`ID_`, `TARGET`, `TARGET_EXCLUDE`, `CONDITION_IDS`, `CONDITION_PARAM`, `ACTION_IDS`, `ACTION_PARAM`, `NAME`, `DESCRIPTION`, `ENABLED`) VALUES (5, '.*', NULL, '4', 'percentIncrease=4,minutesToCompare=10', '2', NULL, 'dubbo method elapsed average with several minutes ago', '监控success average和几分钟前比的冲高', 'Y');
INSERT INTO `T_ALT_ALERT` (`ID_`, `TARGET`, `TARGET_EXCLUDE`, `CONDITION_IDS`, `CONDITION_PARAM`, `ACTION_IDS`, `ACTION_PARAM`, `NAME`, `DESCRIPTION`, `ENABLED`) VALUES (6, '.*', NULL, '5', 'percentIncrease=4,daysToCompare=1,minScope=-5', '2', NULL, 'dubbo method elapsed average with several days ago', '监控elapsed average和几天前相同时间段的冲高', 'Y');

