CREATE DATABASE `booking` /*!40100 DEFAULT CHARACTER SET utf16 */;

CREATE USER 'booking'@'%' 
  IDENTIFIED WITH 'caching_sha2_password' AS '$A$005$!|u1:f)%N<%/+2gITudWXWtLafukpuKexUBvRHqly0MZZYnj9qclJ8uwW2' 
  REQUIRE NONE PASSWORD EXPIRE DEFAULT 
  ACCOUNT UNLOCK PASSWORD HISTORY DEFAULT PASSWORD REUSE INTERVAL DEFAULT PASSWORD REQUIRE CURRENT DEFAULT;
  
GRANT SELECT, INSERT, UPDATE, DELETE ON `booking`.* TO `booking`@`%`

CREATE TABLE `booking`.`resource` (
  `id` VARCHAR(36) NOT NULL,
  `name` VARCHAR(40) NOT NULL,
  PRIMARY KEY (`id`));


CREATE TABLE `reservation` (
  `id` varchar(36) NOT NULL,
  `email` varchar(40) NOT NULL,
  `name` varchar(40) NOT NULL,
  `arrivalDate` datetime NOT NULL,
  `departureDate` datetime NOT NULL,
  `resourceId` varchar(36) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `FK_resource_idx` (`resourceId`),
  KEY `IDX_arrivalDate` (`arrivalDate`),
  KEY `IDX_departureDate` (`departureDate`),
  CONSTRAINT `FK_resource` FOREIGN KEY (`resourceId`) REFERENCES `resource` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf16;

CREATE TABLE `availability` (
  `resourceId` varchar(36) NOT NULL,
  `date` datetime NOT NULL,
  `reservationId` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`resourceId`,`date`),
  KEY `FK_reservation_idx` (`reservationId`),
  CONSTRAINT `FK_reservation` FOREIGN KEY (`reservationId`) REFERENCES `reservation` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf16;


insert into availability (resourceId, date, reservationId) values (select id from resource where name = 'CAMPSITE', DATE_ADD(CURDATE(), INTERVAL 1 DAY), null);
insert into availability (resourceId, date, reservationId) values (select id from resource where name = 'CAMPSITE', DATE_ADD(CURDATE(), INTERVAL 2 DAY), null);
insert into availability (resourceId, date, reservationId) values (select id from resource where name = 'CAMPSITE', DATE_ADD(CURDATE(), INTERVAL 3 DAY), null);
insert into availability (resourceId, date, reservationId) values (select id from resource where name = 'CAMPSITE', DATE_ADD(CURDATE(), INTERVAL 4 DAY), null);
insert into availability (resourceId, date, reservationId) values (select id from resource where name = 'CAMPSITE', DATE_ADD(CURDATE(), INTERVAL 5 DAY), null);
insert into availability (resourceId, date, reservationId) values (select id from resource where name = 'CAMPSITE', DATE_ADD(CURDATE(), INTERVAL 6 DAY), null);
insert into availability (resourceId, date, reservationId) values (select id from resource where name = 'CAMPSITE', DATE_ADD(CURDATE(), INTERVAL 7 DAY), null);
insert into availability (resourceId, date, reservationId) values (select id from resource where name = 'CAMPSITE', DATE_ADD(CURDATE(), INTERVAL 8 DAY), null);
insert into availability (resourceId, date, reservationId) values (select id from resource where name = 'CAMPSITE', DATE_ADD(CURDATE(), INTERVAL 9 DAY), null);
insert into availability (resourceId, date, reservationId) values (select id from resource where name = 'CAMPSITE', DATE_ADD(CURDATE(), INTERVAL 10 DAY), null);
insert into availability (resourceId, date, reservationId) values (select id from resource where name = 'CAMPSITE', DATE_ADD(CURDATE(), INTERVAL 11 DAY), null);
insert into availability (resourceId, date, reservationId) values (select id from resource where name = 'CAMPSITE', DATE_ADD(CURDATE(), INTERVAL 12 DAY), null);
insert into availability (resourceId, date, reservationId) values (select id from resource where name = 'CAMPSITE', DATE_ADD(CURDATE(), INTERVAL 13 DAY), null);
insert into availability (resourceId, date, reservationId) values (select id from resource where name = 'CAMPSITE', DATE_ADD(CURDATE(), INTERVAL 14 DAY), null);
insert into availability (resourceId, date, reservationId) values (select id from resource where name = 'CAMPSITE', DATE_ADD(CURDATE(), INTERVAL 15 DAY), null);
insert into availability (resourceId, date, reservationId) values (select id from resource where name = 'CAMPSITE', DATE_ADD(CURDATE(), INTERVAL 16 DAY), null);
insert into availability (resourceId, date, reservationId) values (select id from resource where name = 'CAMPSITE', DATE_ADD(CURDATE(), INTERVAL 17 DAY), null);
insert into availability (resourceId, date, reservationId) values (select id from resource where name = 'CAMPSITE', DATE_ADD(CURDATE(), INTERVAL 18 DAY), null);
insert into availability (resourceId, date, reservationId) values (select id from resource where name = 'CAMPSITE', DATE_ADD(CURDATE(), INTERVAL 19 DAY), null);
insert into availability (resourceId, date, reservationId) values (select id from resource where name = 'CAMPSITE', DATE_ADD(CURDATE(), INTERVAL 20 DAY), null);
insert into availability (resourceId, date, reservationId) values (select id from resource where name = 'CAMPSITE', DATE_ADD(CURDATE(), INTERVAL 21 DAY), null);
insert into availability (resourceId, date, reservationId) values (select id from resource where name = 'CAMPSITE', DATE_ADD(CURDATE(), INTERVAL 22 DAY), null);
insert into availability (resourceId, date, reservationId) values (select id from resource where name = 'CAMPSITE', DATE_ADD(CURDATE(), INTERVAL 23 DAY), null);
insert into availability (resourceId, date, reservationId) values (select id from resource where name = 'CAMPSITE', DATE_ADD(CURDATE(), INTERVAL 24 DAY), null);
insert into availability (resourceId, date, reservationId) values (select id from resource where name = 'CAMPSITE', DATE_ADD(CURDATE(), INTERVAL 25 DAY), null);
insert into availability (resourceId, date, reservationId) values (select id from resource where name = 'CAMPSITE', DATE_ADD(CURDATE(), INTERVAL 26 DAY), null);
insert into availability (resourceId, date, reservationId) values (select id from resource where name = 'CAMPSITE', DATE_ADD(CURDATE(), INTERVAL 27 DAY), null);
insert into availability (resourceId, date, reservationId) values (select id from resource where name = 'CAMPSITE', DATE_ADD(CURDATE(), INTERVAL 28 DAY), null);
insert into availability (resourceId, date, reservationId) values (select id from resource where name = 'CAMPSITE', DATE_ADD(CURDATE(), INTERVAL 29 DAY), null);
insert into availability (resourceId, date, reservationId) values (select id from resource where name = 'CAMPSITE', DATE_ADD(CURDATE(), INTERVAL 30 DAY), null);