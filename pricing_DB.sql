-- MySQL dump 10.13  Distrib 5.5.44, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: pricing_DB
-- ------------------------------------------------------
-- Server version	5.5.44-0ubuntu0.12.04.1-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Temporary table structure for view `adi_pt3_hotels`
--

DROP TABLE IF EXISTS `adi_pt3_hotels`;
/*!50001 DROP VIEW IF EXISTS `adi_pt3_hotels`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `adi_pt3_hotels` (
  `id` tinyint NOT NULL,
  `categoria` tinyint NOT NULL,
  `country` tinyint NOT NULL,
  `aal1` tinyint NOT NULL,
  `aal2` tinyint NOT NULL,
  `locality` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `adi_pt3_prices`
--

DROP TABLE IF EXISTS `adi_pt3_prices`;
/*!50001 DROP VIEW IF EXISTS `adi_pt3_prices`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `adi_pt3_prices` (
  `id_hotel` tinyint NOT NULL,
  `collect_date` tinyint NOT NULL,
  `checkin_date` tinyint NOT NULL,
  `precio_HDE` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `adi_pt3_ratings`
--

DROP TABLE IF EXISTS `adi_pt3_ratings`;
/*!50001 DROP VIEW IF EXISTS `adi_pt3_ratings`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `adi_pt3_ratings` (
  `id_hotel` tinyint NOT NULL,
  `rating_date` tinyint NOT NULL,
  `segment` tinyint NOT NULL,
  `comfort` tinyint NOT NULL,
  `location` tinyint NOT NULL,
  `clean` tinyint NOT NULL,
  `services` tinyint NOT NULL,
  `staff` tinyint NOT NULL,
  `value` tinyint NOT NULL,
  `wifi` tinyint NOT NULL,
  `average` tinyint NOT NULL,
  `num_users` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `censo_hoteles_NP`
--

DROP TABLE IF EXISTS `censo_hoteles_NP`;
/*!50001 DROP VIEW IF EXISTS `censo_hoteles_NP`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `censo_hoteles_NP` (
  `id_accommodation` tinyint NOT NULL,
  `name` tinyint NOT NULL,
  `category` tinyint NOT NULL,
  `NUTS3` tinyint NOT NULL,
  `locality` tinyint NOT NULL,
  `latitude` tinyint NOT NULL,
  `longitude` tinyint NOT NULL,
  `gm_country` tinyint NOT NULL,
  `gm_aal1` tinyint NOT NULL,
  `gm_aal2` tinyint NOT NULL,
  `gm_aal3` tinyint NOT NULL,
  `gm_aal4` tinyint NOT NULL,
  `gm_locality` tinyint NOT NULL,
  `gm_route` tinyint NOT NULL,
  `gm_street_number` tinyint NOT NULL,
  `gm_postal_code` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `d_accommodation`
--

DROP TABLE IF EXISTS `d_accommodation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `d_accommodation` (
  `id_accommodation` mediumint(9) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `address` varchar(45) DEFAULT NULL,
  `zip_code` varchar(15) DEFAULT NULL,
  `locality` varchar(45) DEFAULT NULL,
  `id_geography` smallint(6) NOT NULL,
  `category` tinyint(4) DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `id_accommodation_type` int(4) NOT NULL,
  `id_accommodation_chain` smallint(6) DEFAULT NULL,
  `gm_country` varchar(100) DEFAULT NULL,
  `gm_aal1` varchar(200) DEFAULT NULL,
  `gm_aal2` varchar(200) DEFAULT NULL,
  `gm_aal3` varchar(200) DEFAULT NULL,
  `gm_aal4` varchar(200) DEFAULT NULL,
  `gm_locality` varchar(200) DEFAULT NULL,
  `gm_route` varchar(300) DEFAULT NULL,
  `gm_street_number` varchar(100) DEFAULT NULL,
  `gm_postal_code` varchar(10) DEFAULT NULL,
  `locked` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id_accommodation`),
  KEY `fk_d_accommodation_d_accommodation_type` (`id_accommodation_type`),
  KEY `fk_d_accommodation_d_geography` (`id_geography`),
  KEY `fk_d_accommodation_geo` (`gm_country`,`gm_aal1`,`gm_aal2`,`gm_aal3`,`gm_locality`)
) ENGINE=InnoDB AUTO_INCREMENT=404789 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `d_accommodation_type`
--

DROP TABLE IF EXISTS `d_accommodation_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `d_accommodation_type` (
  `id_accommodation_type` int(4) NOT NULL,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`id_accommodation_type`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `d_channel`
--

DROP TABLE IF EXISTS `d_channel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `d_channel` (
  `id_channel` tinyint(4) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  PRIMARY KEY (`id_channel`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `d_geo`
--

DROP TABLE IF EXISTS `d_geo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `d_geo` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `country` varchar(100) DEFAULT NULL,
  `aal1` varchar(100) DEFAULT NULL,
  `aal2` varchar(200) DEFAULT NULL,
  `aal3` varchar(200) DEFAULT NULL,
  `aal4` varchar(200) DEFAULT NULL,
  `locality` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=24545 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `d_geo_aggregations`
--

DROP TABLE IF EXISTS `d_geo_aggregations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `d_geo_aggregations` (
  `country` varchar(100) NOT NULL,
  `aal1` varchar(200) NOT NULL,
  `aal2` varchar(200) DEFAULT NULL,
  `aal3` varchar(200) DEFAULT NULL,
  `aal4` varchar(200) DEFAULT NULL,
  `locality` varchar(200) DEFAULT NULL,
  `item_key` varchar(100) NOT NULL,
  `item_value` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `d_geography`
--

DROP TABLE IF EXISTS `d_geography`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `d_geography` (
  `id_geography` smallint(6) NOT NULL AUTO_INCREMENT,
  `country` varchar(45) NOT NULL,
  `NUTS1` varchar(45) DEFAULT NULL,
  `NUTS2` varchar(45) DEFAULT NULL,
  `NUTS3` varchar(45) DEFAULT NULL,
  `locality` varchar(45) DEFAULT NULL,
  `id_geography_channel` varchar(9) DEFAULT NULL,
  PRIMARY KEY (`id_geography`)
) ENGINE=InnoDB AUTO_INCREMENT=554 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `d_ine`
--

DROP TABLE IF EXISTS `d_ine`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `d_ine` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `country` varchar(100) DEFAULT NULL,
  `aal1` varchar(100) DEFAULT NULL,
  `aal2` varchar(200) DEFAULT NULL,
  `locality` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8120 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `d_ine14`
--

DROP TABLE IF EXISTS `d_ine14`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `d_ine14` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `country` varchar(100) DEFAULT NULL,
  `aal1` varchar(100) DEFAULT NULL,
  `aal2` varchar(200) DEFAULT NULL,
  `locality` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8122 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `d_ine_ccaa`
--

DROP TABLE IF EXISTS `d_ine_ccaa`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `d_ine_ccaa` (
  `codccaa` varchar(2) NOT NULL,
  `ccaa` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`codccaa`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `d_ine_municipios`
--

DROP TABLE IF EXISTS `d_ine_municipios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `d_ine_municipios` (
  `codprov` varchar(4) NOT NULL,
  `municipio` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `d_ine_provincias`
--

DROP TABLE IF EXISTS `d_ine_provincias`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `d_ine_provincias` (
  `codprov` varchar(4) NOT NULL,
  `codccaa` varchar(4) DEFAULT NULL,
  `provincia` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`codprov`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `d_italy_comune_20150130`
--

DROP TABLE IF EXISTS `d_italy_comune_20150130`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `d_italy_comune_20150130` (
  `id_region` varchar(10) NOT NULL,
  `id_province` varchar(10) NOT NULL,
  `id_comune` varchar(10) NOT NULL,
  `comune` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id_region`,`id_province`,`id_comune`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `d_italy_province_20150130`
--

DROP TABLE IF EXISTS `d_italy_province_20150130`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `d_italy_province_20150130` (
  `id_region` varchar(10) NOT NULL,
  `id_province` varchar(10) NOT NULL,
  `province` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id_province`,`id_region`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `d_italy_region_20150130`
--

DROP TABLE IF EXISTS `d_italy_region_20150130`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `d_italy_region_20150130` (
  `id_region` varchar(10) NOT NULL,
  `region` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id_region`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `d_product`
--

DROP TABLE IF EXISTS `d_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `d_product` (
  `id_product` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `adult_amount` tinyint(4) NOT NULL,
  `children_amount` tinyint(4) NOT NULL,
  `breakfast_price` smallint(6) NOT NULL,
  `breakfast_included` tinyint(1) NOT NULL,
  `half_board` tinyint(1) NOT NULL,
  `full_board` tinyint(1) NOT NULL,
  `all_inclusive` tinyint(1) NOT NULL,
  `free_cancellation` tinyint(1) NOT NULL,
  `pay_stay` tinyint(1) NOT NULL,
  `pay_later` tinyint(1) NOT NULL,
  `non_refundable` tinyint(1) NOT NULL,
  PRIMARY KEY (`id_product`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `d_service`
--

DROP TABLE IF EXISTS `d_service`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `d_service` (
  `id_service` mediumint(9) NOT NULL AUTO_INCREMENT,
  `description` varchar(45) NOT NULL,
  `id_service_category` tinyint(4) NOT NULL,
  PRIMARY KEY (`id_service`),
  KEY `fk_d_service_d_service_category` (`id_service_category`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `d_visitor_segment`
--

DROP TABLE IF EXISTS `d_visitor_segment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `d_visitor_segment` (
  `id_visitor_segment` tinyint(4) NOT NULL AUTO_INCREMENT,
  `unique_visitor_segment` tinyint(4) DEFAULT NULL,
  `segment` varchar(45) NOT NULL,
  `subsegment` varchar(45) DEFAULT NULL,
  `clean_ES` varchar(100) DEFAULT NULL,
  `clean_EN` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id_visitor_segment`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `error_refs`
--

DROP TABLE IF EXISTS `error_refs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `error_refs` (
  `ref_code` int(11) NOT NULL AUTO_INCREMENT,
  `html` text NOT NULL,
  PRIMARY KEY (`ref_code`)
) ENGINE=InnoDB AUTO_INCREMENT=1367013 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ft_accommodation_rating`
--

DROP TABLE IF EXISTS `ft_accommodation_rating`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ft_accommodation_rating` (
  `id_accommodation` mediumint(9) NOT NULL,
  `id_scraping_date` date NOT NULL,
  `id_visitor_segment` float NOT NULL,
  `comfort` float DEFAULT NULL,
  `value` float DEFAULT NULL,
  `location` float DEFAULT NULL,
  `clean` float DEFAULT NULL,
  `services` float DEFAULT NULL,
  `staff` float DEFAULT NULL,
  `wifi` float DEFAULT NULL,
  `average` float DEFAULT '0',
  `ttl_users` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id_accommodation`,`id_scraping_date`,`id_visitor_segment`),
  KEY `fk_ft_booking_rating_d_visitor_segment1` (`id_visitor_segment`),
  KEY `fk_ft_booking_rating_d_accommodation_idx` (`id_accommodation`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ft_accommodation_review`
--

DROP TABLE IF EXISTS `ft_accommodation_review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ft_accommodation_review` (
  `id_review` mediumint(9) NOT NULL AUTO_INCREMENT,
  `id_accommodation` mediumint(9) NOT NULL,
  `id_scraping_date` date NOT NULL,
  `id_visitor_segment` float NOT NULL,
  `type_trip` varchar(100) DEFAULT NULL,
  `type_room` varchar(200) DEFAULT NULL,
  `stay_nights` int(4) DEFAULT NULL,
  `with_pet` varchar(100) DEFAULT NULL,
  `visitor_from` varchar(45) DEFAULT NULL,
  `review_date` date DEFAULT NULL,
  `review_lang` varchar(5) DEFAULT NULL,
  `review_good` varchar(5000) DEFAULT NULL,
  `review_bad` varchar(5000) DEFAULT NULL,
  `score` float DEFAULT '0',
  PRIMARY KEY (`id_review`)
) ENGINE=InnoDB AUTO_INCREMENT=29526 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ft_accommodation_services`
--

DROP TABLE IF EXISTS `ft_accommodation_services`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ft_accommodation_services` (
  `id_accommodation` mediumint(9) NOT NULL,
  `id_scraping_date` date NOT NULL,
  `free_wifi` tinyint(1) NOT NULL,
  `free_parking` tinyint(1) NOT NULL,
  `pets_allowed` tinyint(1) NOT NULL,
  `activities` tinytext,
  PRIMARY KEY (`id_accommodation`,`id_scraping_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ft_admin`
--

DROP TABLE IF EXISTS `ft_admin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ft_admin` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `instance` varchar(45) DEFAULT NULL,
  `start_time` timestamp NULL DEFAULT NULL,
  `end_time` timestamp NULL DEFAULT NULL,
  `total_time` varchar(45) DEFAULT NULL,
  `data_size` double DEFAULT NULL,
  `country` varchar(100) DEFAULT NULL,
  `acc_type` varchar(100) DEFAULT NULL,
  `acc_count` int(11) DEFAULT NULL,
  `rows_total` int(11) DEFAULT NULL,
  `rows_ok` int(11) DEFAULT NULL,
  `rows_ko` int(11) DEFAULT NULL,
  `rows_hde_nf` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1167 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ft_agenda`
--

DROP TABLE IF EXISTS `ft_agenda`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ft_agenda` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `date_from` date DEFAULT NULL,
  `date_to` date DEFAULT NULL,
  `location` varchar(200) DEFAULT NULL,
  `description` varchar(500) DEFAULT NULL,
  `periodicity` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ft_geo_matching`
--

DROP TABLE IF EXISTS `ft_geo_matching`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ft_geo_matching` (
  `type` varchar(50) NOT NULL,
  `country` varchar(100) DEFAULT NULL,
  `aal1` varchar(100) DEFAULT NULL,
  `aal2` varchar(200) DEFAULT NULL,
  `aal3` varchar(200) DEFAULT NULL,
  `aal4` varchar(200) DEFAULT NULL,
  `locality` varchar(200) DEFAULT NULL,
  `bad` varchar(200) NOT NULL,
  `good` varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ft_geo_matching14`
--

DROP TABLE IF EXISTS `ft_geo_matching14`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ft_geo_matching14` (
  `type` varchar(50) NOT NULL,
  `country` varchar(100) DEFAULT NULL,
  `aal1` varchar(100) DEFAULT '',
  `aal2` varchar(200) DEFAULT '',
  `aal3` varchar(200) DEFAULT NULL,
  `aal4` varchar(200) DEFAULT NULL,
  `locality` varchar(200) DEFAULT NULL,
  `bad` varchar(200) NOT NULL,
  `good` varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ft_product_price`
--

DROP TABLE IF EXISTS `ft_product_price`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ft_product_price` (
  `id_product` int(11) NOT NULL,
  `id_accommodation` mediumint(9) NOT NULL,
  `id_channel` tinyint(4) NOT NULL,
  `id_booking_date` date NOT NULL,
  `id_checkin_date` date NOT NULL,
  `lag_days` smallint(6) DEFAULT NULL,
  `los` smallint(6) NOT NULL DEFAULT '1',
  `price` float DEFAULT NULL,
  `price_flag` tinyint(4) DEFAULT '0',
  PRIMARY KEY (`id_product`,`id_accommodation`,`id_channel`,`id_booking_date`,`id_checkin_date`),
  KEY `fk_id_accommodation_idx` (`id_accommodation`),
  KEY `fk_id_channel_idx` (`id_channel`),
  KEY `fk_id_checkin_date_idx` (`id_checkin_date`),
  CONSTRAINT `fk_id_channel` FOREIGN KEY (`id_channel`) REFERENCES `d_channel` (`id_channel`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ft_thresholds`
--

DROP TABLE IF EXISTS `ft_thresholds`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ft_thresholds` (
  `id_accommodation_channel` varchar(45) NOT NULL,
  `id_accommodation` int(11) DEFAULT NULL,
  `max_price` float DEFAULT NULL,
  `threshold` float DEFAULT NULL,
  `update_date` date DEFAULT NULL,
  PRIMARY KEY (`id_accommodation_channel`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ft_translation`
--

DROP TABLE IF EXISTS `ft_translation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ft_translation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_hotel` int(11) DEFAULT NULL,
  `id_accommodation` int(11) DEFAULT NULL,
  `id_accommodation_channel` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=499 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ftpp_aggregated`
--

DROP TABLE IF EXISTS `ftpp_aggregated`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ftpp_aggregated` (
  `id_accommodation_type` int(4) NOT NULL,
  `country` varchar(100) NOT NULL,
  `aal1` varchar(100) NOT NULL,
  `aal2` varchar(200) NOT NULL,
  `locality` varchar(200) NOT NULL,
  `id_product` int(2) NOT NULL,
  `category` tinyint(4) NOT NULL,
  `id_checkin_date` date NOT NULL,
  `price` float NOT NULL,
  PRIMARY KEY (`id_accommodation_type`,`country`,`aal1`,`aal2`,`locality`,`id_product`,`category`,`id_checkin_date`,`price`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ftpp_aggregated_hist`
--

DROP TABLE IF EXISTS `ftpp_aggregated_hist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ftpp_aggregated_hist` (
  `id_accommodation_type` int(4) NOT NULL,
  `country` varchar(100) NOT NULL,
  `aal1` varchar(100) NOT NULL,
  `aal2` varchar(200) NOT NULL,
  `locality` varchar(200) NOT NULL,
  `id_product` int(2) NOT NULL,
  `category` tinyint(4) NOT NULL,
  `id_checkin_date` date NOT NULL,
  `price` float NOT NULL,
  PRIMARY KEY (`id_accommodation_type`,`country`,`aal1`,`aal2`,`locality`,`id_product`,`category`,`id_checkin_date`,`price`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ftpp_lite`
--

DROP TABLE IF EXISTS `ftpp_lite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ftpp_lite` (
  `id_accommodation` mediumint(9) NOT NULL,
  `id_product` int(2) NOT NULL,
  `id_booking_date` date NOT NULL,
  `id_checkin_date` date NOT NULL,
  `price` float DEFAULT NULL,
  PRIMARY KEY (`id_accommodation`,`id_product`,`id_booking_date`,`id_checkin_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `nm_accommodation_channel`
--

DROP TABLE IF EXISTS `nm_accommodation_channel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `nm_accommodation_channel` (
  `id_accommodation` mediumint(9) NOT NULL,
  `id_channel` tinyint(4) NOT NULL,
  `id_accommodation_channel` varchar(79) NOT NULL,
  `url_accommodation_channel` varchar(255) NOT NULL,
  `room_amount` smallint(6) DEFAULT NULL,
  `first_seen` date DEFAULT NULL,
  `last_seen` date DEFAULT NULL,
  PRIMARY KEY (`id_accommodation`,`id_channel`),
  UNIQUE KEY `id_accommodation_channel_UNIQUE` (`id_accommodation_channel`),
  KEY `fk_nm_id_accommodation_channel_d_accommodation` (`id_accommodation`),
  KEY `fk_id_channel_idx` (`id_channel`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `nm_geography_channel`
--

DROP TABLE IF EXISTS `nm_geography_channel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `nm_geography_channel` (
  `id_geography` mediumint(9) NOT NULL,
  `id_channel` tinyint(4) NOT NULL,
  `id_geography_channel` varchar(9) NOT NULL,
  PRIMARY KEY (`id_geography`,`id_channel`),
  KEY `fk_nm_id_geography_chanel_d_geography` (`id_geography`),
  KEY `fk_nm_id_geography_channel_d_channel` (`id_channel`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary table structure for view `ratings_NP`
--

DROP TABLE IF EXISTS `ratings_NP`;
/*!50001 DROP VIEW IF EXISTS `ratings_NP`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `ratings_NP` (
  `id_accommodation` tinyint NOT NULL,
  `name` tinyint NOT NULL,
  `id_scraping_date` tinyint NOT NULL,
  `segment` tinyint NOT NULL,
  `comfort` tinyint NOT NULL,
  `value` tinyint NOT NULL,
  `location` tinyint NOT NULL,
  `clean` tinyint NOT NULL,
  `services` tinyint NOT NULL,
  `staff` tinyint NOT NULL,
  `wifi` tinyint NOT NULL,
  `average` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `scraping_errors`
--

DROP TABLE IF EXISTS `scraping_errors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `scraping_errors` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `task` varchar(45) NOT NULL,
  `hotel` varchar(255) DEFAULT NULL,
  `description` varchar(500) NOT NULL,
  `ref_code` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_ref_code_idx` (`ref_code`)
) ENGINE=InnoDB AUTO_INCREMENT=2379113 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Final view structure for view `adi_pt3_hotels`
--

/*!50001 DROP TABLE IF EXISTS `adi_pt3_hotels`*/;
/*!50001 DROP VIEW IF EXISTS `adi_pt3_hotels`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`developer`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `adi_pt3_hotels` AS select `da`.`id_accommodation` AS `id`,`da`.`category` AS `categoria`,`da`.`gm_country` AS `country`,`da`.`gm_aal1` AS `aal1`,`da`.`gm_aal2` AS `aal2`,`da`.`gm_locality` AS `locality` from `d_accommodation` `da` where ((`da`.`id_accommodation_type` = 204) and (((`da`.`gm_country` = 'Spain') and (`da`.`gm_aal1` = 'Madrid, Comunidad de') and (`da`.`gm_aal2` = 'Madrid') and (`da`.`gm_locality` = 'Madrid')) or ((`da`.`gm_country` = 'Spain') and (`da`.`gm_aal1` = 'Cataluña') and (`da`.`gm_aal2` = 'Barcelona') and (`da`.`gm_locality` = 'Barcelona')) or ((`da`.`gm_country` = 'Spain') and (`da`.`gm_aal1` = 'País Vasco') and (`da`.`gm_aal2` = 'Bizkaia') and (`da`.`gm_locality` = 'Bilbao')) or ((`da`.`gm_country` = 'Spain') and (`da`.`gm_aal1` = 'Balears, Illes') and (`da`.`gm_aal2` = 'Balears, Illes') and (`da`.`gm_locality` = 'Palma de Mallorca')) or ((`da`.`gm_country` = 'Spain') and (`da`.`gm_aal1` = 'Comunitat Valenciana') and (`da`.`gm_aal2` = 'Alicante/Alacant') and (`da`.`gm_locality` = 'Benidorm')) or ((`da`.`gm_country` = 'Spain') and (`da`.`gm_aal1` = 'Andalucía') and (`da`.`gm_aal2` = 'Málaga') and (`da`.`gm_locality` = 'Málaga')))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `adi_pt3_prices`
--

/*!50001 DROP TABLE IF EXISTS `adi_pt3_prices`*/;
/*!50001 DROP VIEW IF EXISTS `adi_pt3_prices`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`developer`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `adi_pt3_prices` AS select `ftpp`.`id_accommodation` AS `id_hotel`,`ftpp`.`id_booking_date` AS `collect_date`,`ftpp`.`id_checkin_date` AS `checkin_date`,`ftpp`.`price` AS `precio_HDE` from (`ft_product_price` `ftpp` join `d_accommodation` `da` on((`ftpp`.`id_accommodation` = `da`.`id_accommodation`))) where (((cast(`ftpp`.`id_checkin_date` as date) like '2014-12%') or (cast(`ftpp`.`id_checkin_date` as date) like '2015-01%') or (cast(`ftpp`.`id_checkin_date` as date) like '2015-02%') or (cast(`ftpp`.`id_checkin_date` as date) like '2015-03%') or (cast(`ftpp`.`id_checkin_date` as date) like '2015-04%')) and (`da`.`id_accommodation_type` = 204) and (((`da`.`gm_country` = 'Spain') and (`da`.`gm_aal1` = 'Madrid, Comunidad de') and (`da`.`gm_aal2` = 'Madrid') and (`da`.`gm_locality` = 'Madrid')) or ((`da`.`gm_country` = 'Spain') and (`da`.`gm_aal1` = 'Cataluña') and (`da`.`gm_aal2` = 'Barcelona') and (`da`.`gm_locality` = 'Barcelona')) or ((`da`.`gm_country` = 'Spain') and (`da`.`gm_aal1` = 'País Vasco') and (`da`.`gm_aal2` = 'Bizkaia') and (`da`.`gm_locality` = 'Bilbao')) or ((`da`.`gm_country` = 'Spain') and (`da`.`gm_aal1` = 'Balears, Illes') and (`da`.`gm_aal2` = 'Balears, Illes') and (`da`.`gm_locality` = 'Palma de Mallorca')) or ((`da`.`gm_country` = 'Spain') and (`da`.`gm_aal1` = 'Comunitat Valenciana') and (`da`.`gm_aal2` = 'Alicante/Alacant') and (`da`.`gm_locality` = 'Benidorm')) or ((`da`.`gm_country` = 'Spain') and (`da`.`gm_aal1` = 'Andalucía') and (`da`.`gm_aal2` = 'Málaga') and (`da`.`gm_locality` = 'Málaga')))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `adi_pt3_ratings`
--

/*!50001 DROP TABLE IF EXISTS `adi_pt3_ratings`*/;
/*!50001 DROP VIEW IF EXISTS `adi_pt3_ratings`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`developer`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `adi_pt3_ratings` AS select `ftar`.`id_accommodation` AS `id_hotel`,`ftar`.`id_scraping_date` AS `rating_date`,`dvs`.`clean_ES` AS `segment`,`ftar`.`comfort` AS `comfort`,`ftar`.`location` AS `location`,`ftar`.`clean` AS `clean`,`ftar`.`services` AS `services`,`ftar`.`staff` AS `staff`,`ftar`.`value` AS `value`,`ftar`.`wifi` AS `wifi`,`ftar`.`average` AS `average`,`ftar`.`ttl_users` AS `num_users` from ((`ft_accommodation_rating` `ftar` join `d_accommodation` `da` on((`ftar`.`id_accommodation` = `da`.`id_accommodation`))) join `d_visitor_segment` `dvs` on((`dvs`.`id_visitor_segment` = `ftar`.`id_visitor_segment`))) where (((cast(`ftar`.`id_scraping_date` as date) like '2014-12%') or (cast(`ftar`.`id_scraping_date` as date) like '2015-01%') or (cast(`ftar`.`id_scraping_date` as date) like '2015-02%') or (cast(`ftar`.`id_scraping_date` as date) like '2015-03%') or (cast(`ftar`.`id_scraping_date` as date) like '2015-04%')) and (`da`.`id_accommodation_type` = 204) and (((`da`.`gm_country` = 'Spain') and (`da`.`gm_aal1` = 'Madrid, Comunidad de') and (`da`.`gm_aal2` = 'Madrid') and (`da`.`gm_locality` = 'Madrid')) or ((`da`.`gm_country` = 'Spain') and (`da`.`gm_aal1` = 'Cataluña') and (`da`.`gm_aal2` = 'Barcelona') and (`da`.`gm_locality` = 'Barcelona')) or ((`da`.`gm_country` = 'Spain') and (`da`.`gm_aal1` = 'País Vasco') and (`da`.`gm_aal2` = 'Bizkaia') and (`da`.`gm_locality` = 'Bilbao')) or ((`da`.`gm_country` = 'Spain') and (`da`.`gm_aal1` = 'Balears, Illes') and (`da`.`gm_aal2` = 'Balears, Illes') and (`da`.`gm_locality` = 'Palma de Mallorca')) or ((`da`.`gm_country` = 'Spain') and (`da`.`gm_aal1` = 'Comunitat Valenciana') and (`da`.`gm_aal2` = 'Alicante/Alacant') and (`da`.`gm_locality` = 'Benidorm')) or ((`da`.`gm_country` = 'Spain') and (`da`.`gm_aal1` = 'Andalucía') and (`da`.`gm_aal2` = 'Málaga') and (`da`.`gm_locality` = 'Málaga')))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `censo_hoteles_NP`
--

/*!50001 DROP TABLE IF EXISTS `censo_hoteles_NP`*/;
/*!50001 DROP VIEW IF EXISTS `censo_hoteles_NP`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`developer`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `censo_hoteles_NP` AS select `da`.`id_accommodation` AS `id_accommodation`,`da`.`name` AS `name`,`da`.`category` AS `category`,`dg`.`NUTS3` AS `NUTS3`,`dg`.`locality` AS `locality`,`da`.`latitude` AS `latitude`,`da`.`longitude` AS `longitude`,`da`.`gm_country` AS `gm_country`,`da`.`gm_aal1` AS `gm_aal1`,`da`.`gm_aal2` AS `gm_aal2`,`da`.`gm_aal3` AS `gm_aal3`,`da`.`gm_aal4` AS `gm_aal4`,`da`.`gm_locality` AS `gm_locality`,`da`.`gm_route` AS `gm_route`,`da`.`gm_street_number` AS `gm_street_number`,`da`.`gm_postal_code` AS `gm_postal_code` from (`d_accommodation` `da` join `d_geography` `dg` on((`da`.`id_geography` = `dg`.`id_geography`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `ratings_NP`
--

/*!50001 DROP TABLE IF EXISTS `ratings_NP`*/;
/*!50001 DROP VIEW IF EXISTS `ratings_NP`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`developer`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `ratings_NP` AS select `ft_accommodation_rating`.`id_accommodation` AS `id_accommodation`,`d_accommodation`.`name` AS `name`,`ft_accommodation_rating`.`id_scraping_date` AS `id_scraping_date`,`d_visitor_segment`.`segment` AS `segment`,`ft_accommodation_rating`.`comfort` AS `comfort`,`ft_accommodation_rating`.`value` AS `value`,`ft_accommodation_rating`.`location` AS `location`,`ft_accommodation_rating`.`clean` AS `clean`,`ft_accommodation_rating`.`services` AS `services`,`ft_accommodation_rating`.`staff` AS `staff`,`ft_accommodation_rating`.`wifi` AS `wifi`,`ft_accommodation_rating`.`average` AS `average` from ((`ft_accommodation_rating` join `d_visitor_segment` on((`ft_accommodation_rating`.`id_visitor_segment` = `d_visitor_segment`.`id_visitor_segment`))) join `d_accommodation` on((`d_accommodation`.`id_accommodation` = `ft_accommodation_rating`.`id_accommodation`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-10-06 13:04:04
