CREATE TABLE `T_ALT_CONDITION` (
  `ID_` bigint(20) NOT NULL AUTO_INCREMENT,
  `CONTENT_TYPE` varchar(255) DEFAULT NULL,
  `CONTENT` varchar(255) DEFAULT NULL,
  `DESCRIPTION` varchar(255) DEFAULT NULL,
  `ENABLED` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID_`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;



INSERT INTO `T_ALT_CONDITION` (`ID_`, `CONTENT_TYPE`, `CONTENT`, `DESCRIPTION`, `ENABLED`) VALUES (1, 'JAVA_CLASS', 'com.lvmama.soa.monitor.service.alert.condition.impl.MethodSuccessTimesTodayCondition', '监控success times和前几分钟相比的上升速度', 'Y');
INSERT INTO `T_ALT_CONDITION` (`ID_`, `CONTENT_TYPE`, `CONTENT`, `DESCRIPTION`, `ENABLED`) VALUES (2, 'JAVA_CLASS', 'com.lvmama.soa.monitor.service.alert.condition.impl.MethodSuccessTimesWithOtherDaysCondition', '监控success times和前几天相比的上升速度 2', 'Y');
INSERT INTO `T_ALT_CONDITION` (`ID_`, `CONTENT_TYPE`, `CONTENT`, `DESCRIPTION`, `ENABLED`) VALUES (3, 'JAVA_CLASS', 'com.lvmama.soa.monitor.service.alert.condition.impl.MethodSuccessTimesThresholdCondition', '监控method的每分钟success times是否超过阈值', 'Y');
INSERT INTO `T_ALT_CONDITION` (`ID_`, `CONTENT_TYPE`, `CONTENT`, `DESCRIPTION`, `ENABLED`) VALUES (4, 'JAVA_CLASS', 'com.lvmama.soa.monitor.service.alert.condition.impl.MethodElapsedAvgTodayCondition', '监控elapsed average和前几分钟相比的上升速度', 'Y');
INSERT INTO `T_ALT_CONDITION` (`ID_`, `CONTENT_TYPE`, `CONTENT`, `DESCRIPTION`, `ENABLED`) VALUES (5, 'JAVA_CLASS', 'com.lvmama.soa.monitor.service.alert.condition.impl.MethodElapsedAvgWithOtherDaysCondition', '监控elapsed average和前几天相比的上升速度', 'Y');
