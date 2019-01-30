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
