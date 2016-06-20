CREATE TABLE `T_ALT_RECORD` (
  `ID_` bigint(20) NOT NULL AUTO_INCREMENT,
  `APP_NAME` varchar(255) DEFAULT NULL,
  `SERVICE` varchar(255) DEFAULT NULL,
  `METHOD` varchar(255) DEFAULT NULL,
  `CONSUMER_IP` varchar(255) DEFAULT NULL,
  `PROVIDER_IP` varchar(255) DEFAULT NULL,
  `ALERT_MSG` text,
  `INSERT_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`ID_`),
  KEY `IDX_TIME` (`INSERT_TIME`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
