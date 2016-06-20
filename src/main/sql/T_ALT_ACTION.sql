CREATE TABLE `T_ALT_ACTION` (
  `ID_` bigint(20) NOT NULL AUTO_INCREMENT,
  `CONTENT_TYPE` varchar(255) DEFAULT NULL,
  `CONTENT` varchar(255) DEFAULT NULL,
  `DESCRIPTION` varchar(255) DEFAULT NULL,
  `ENABLED` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID_`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;



INSERT INTO `T_ALT_ACTION` (`ID_`, `CONTENT_TYPE`, `CONTENT`, `DESCRIPTION`, `ENABLED`) VALUES (1, 'JAVA_CLASS', 'com.lvmama.soa.monitor.service.alert.action.impl.MethodSuccessTimesLogAction', '在后台打印log', 'N');
INSERT INTO `T_ALT_ACTION` (`ID_`, `CONTENT_TYPE`, `CONTENT`, `DESCRIPTION`, `ENABLED`) VALUES (2, 'JAVA_CLASS', 'com.lvmama.soa.monitor.service.alert.action.impl.MethodSuccessTimesSaveToDBAction', '存入T_ALT_RECORD', 'Y');
