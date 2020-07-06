-- MySQL dump 10.13  Distrib 5.7.30, for Linux (x86_64)
--
-- Host: 127.0.0.1    Database: ad_1cbfb1057e12ba7
-- ------------------------------------------------------
-- Server version	5.5.62-log

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
-- Table structure for table `compte`
--

DROP TABLE IF EXISTS `compte`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `compte` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `activate` varchar(255) DEFAULT NULL,
  `login` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `profile_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKmmhw4hs6ep09qqjm6cn8a0cyn` (`profile_id`),
  CONSTRAINT `FKmmhw4hs6ep09qqjm6cn8a0cyn` FOREIGN KEY (`profile_id`) REFERENCES `profile` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=281 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `compte`
--

LOCK TABLES `compte` WRITE;
/*!40000 ALTER TABLE `compte` DISABLE KEYS */;
INSERT INTO `compte` VALUES (86,'yes','karim','karim',1),(94,'yes','yanis','yanis',2),(95,'yes','sebasiane','sebasiane',2),(251,'yes','stephane','stephane',1),(261,'yes','adam','adam',2),(271,'yes','juliette','juliette',2);
/*!40000 ALTER TABLE `compte` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `course`
--

DROP TABLE IF EXISTS `course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `course` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `libelle` varchar(255) DEFAULT NULL,
  `progress` varchar(255) DEFAULT NULL,
  `level` varchar(50) DEFAULT NULL,
  `description` varchar(100) DEFAULT NULL,
  `image` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course`
--

LOCK TABLES `course` WRITE;
/*!40000 ALTER TABLE `course` DISABLE KEYS */;
INSERT INTO `course` VALUES (1,'Maths','0',NULL,'Cours de maths avec plusieurs chapitres: algèbre, arithmetique, etc','https://image.shutterstock.com/image-vector/creative-hand-drawn-vector-maths-260nw-461142283.jpg'),(2,'Français','0',NULL,'Cours de Français avec plusieurs chapitres: Grammaire, Orthographe etc ','https://t3.ftcdn.net/jpg/01/28/45/16/240_F_128451685_HwRiouQcs31bEtAvaixODxVpGvJRMUCU.jpg'),(3,'Physique','0',NULL,'Cours de Physique avec plusieurs chapitres: Electricité, Mécanique etc ','https://www.studyrama.com/IMG/arton89280.jpg'),(4,'Chimie','0',NULL,'Cours de Chimie avec plusieurs chapitres: les composants chimiques, réactions etc ','https://cdn.futura-sciences.com/buildsv6/images/wide1920/b/b/5/bb5ab61f3e_50151085_quiz-chimie-bac-mg-fotolia.jpg'),(5,'Anglais','0',NULL,'Cours d\' ANglais avec plusieurs chapitres: Grammaire, Speaking, reading etc ','https://vraiesecolesdelangues.com/wp-content/uploads/2013/02/learning-english.jpg'),(6,'Histoire','0',NULL,'Cours d\'Histoire avec plusieurs chapitres: Guerres mondialles, crise des années 30 etc ','https://i.f1g.fr/media/eidos/805x453_crop/2017/06/16/XVM2b5c766c-4080-11e7-a469-62c36d07d43b.jpg'),(7,'Géographie','0',NULL,'Cours de Géographie avec plusieurs chapitres: la démographie mondiale etc ','https://www.geodus.com/globe-map/tresgrand/geo_wb34083.jpg'),(8,'Biologie','0',NULL,'Cours de Biologie avec plusieurs chapitres: les plantes, la réproduction etc ','https://www.prf.upol.cz/typo3temp/assets/_processed_/d/e/csm_dna-1811955_1920_7b6c094ae2.jpg');
/*!40000 ALTER TABLE `course` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `course_content`
--

DROP TABLE IF EXISTS `course_content`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `course_content` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `path` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `course_id` int(11) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKknr61ctevkwqhoxi6wpem4dk6` (`course_id`),
  CONSTRAINT `FKknr61ctevkwqhoxi6wpem4dk6` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course_content`
--

LOCK TABLES `course_content` WRITE;
/*!40000 ALTER TABLE `course_content` DISABLE KEYS */;
INSERT INTO `course_content` VALUES (1,'http://www.ancestralauthor.com/tutorial/tutorial.pdf','premier cours',1,1),(2,'http://www.ancestralauthor.com/download/large.pdf','deuxième cours',1,1),(3,'http://www.ancestralauthor.com/download/sample.pdf','troisième cours',1,1),(4,'http://www.ancestralauthor.com/tutorial/tutorial.pdf','Français elementaire',2,1),(5,'https://webusers.imj-prg.fr/~alessandro.chiodo/LU1MA002/exomov/F05E06.mp4','calcul fréquenciel',1,2);
/*!40000 ALTER TABLE `course_content` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `course_progress`
--

DROP TABLE IF EXISTS `course_progress`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `course_progress` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_course` int(11) DEFAULT NULL,
  `id_user` int(11) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course_progress`
--

LOCK TABLES `course_progress` WRITE;
/*!40000 ALTER TABLE `course_progress` DISABLE KEYS */;
INSERT INTO `course_progress` VALUES (1,1,1,'done'),(2,2,1,'notdone'),(3,3,1,'done'),(4,4,1,'done'),(5,5,1,'notdone');
/*!40000 ALTER TABLE `course_progress` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exercice`
--

DROP TABLE IF EXISTS `exercice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `exercice` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `course_id` int(11) DEFAULT NULL,
  `libelle` varchar(255) DEFAULT NULL,
  `note` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exercice`
--

LOCK TABLES `exercice` WRITE;
/*!40000 ALTER TABLE `exercice` DISABLE KEYS */;
INSERT INTO `exercice` VALUES (1,1,'exo1',13.5),(2,1,'exo2',14),(3,2,'exo3',14),(4,2,'exo4',12);
/*!40000 ALTER TABLE `exercice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exercice_progress`
--

DROP TABLE IF EXISTS `exercice_progress`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `exercice_progress` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_course` int(11) DEFAULT NULL,
  `id_exercice` int(11) DEFAULT NULL,
  `id_user` int(11) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exercice_progress`
--

LOCK TABLES `exercice_progress` WRITE;
/*!40000 ALTER TABLE `exercice_progress` DISABLE KEYS */;
INSERT INTO `exercice_progress` VALUES (1,1,1,1,'done'),(2,1,2,1,'notdone');
/*!40000 ALTER TABLE `exercice_progress` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `listchoices`
--

DROP TABLE IF EXISTS `listchoices`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `listchoices` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) DEFAULT NULL,
  `libelle` varchar(255) DEFAULT NULL,
  `qcm_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `listchoices`
--

LOCK TABLES `listchoices` WRITE;
/*!40000 ALTER TABLE `listchoices` DISABLE KEYS */;
INSERT INTO `listchoices` VALUES (1,'A','48',1),(2,'B','28',1),(3,'C','27',1),(4,'D','30',1),(5,'A','25',2),(6,'B','27',2),(7,'C','26',2),(8,'D','30',2);
/*!40000 ALTER TABLE `listchoices` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `log_activity`
--

DROP TABLE IF EXISTS `log_activity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `log_activity` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `date_activity` datetime DEFAULT NULL,
  `libelle` varchar(255) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKe2pv8h1erkw0r69yvowo6plj5` (`user_id`),
  CONSTRAINT `FKe2pv8h1erkw0r69yvowo6plj5` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `log_activity`
--

LOCK TABLES `log_activity` WRITE;
/*!40000 ALTER TABLE `log_activity` DISABLE KEYS */;
INSERT INTO `log_activity` VALUES (1,'2020-05-27 00:00:00','premier cours',291);
/*!40000 ALTER TABLE `log_activity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `log_session`
--

DROP TABLE IF EXISTS `log_session`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `log_session` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `date_connexion` datetime DEFAULT NULL,
  `date_deconnexion` datetime DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKpesweraixtga75rqbhuepb5un` (`user_id`),
  CONSTRAINT `FKpesweraixtga75rqbhuepb5un` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `log_session`
--

LOCK TABLES `log_session` WRITE;
/*!40000 ALTER TABLE `log_session` DISABLE KEYS */;
INSERT INTO `log_session` VALUES (1,'2020-05-27 17:17:33','2020-05-27 17:28:23',281),(11,'2020-05-27 17:22:32',NULL,291),(21,'2020-05-27 17:23:36',NULL,301),(31,'2020-05-27 17:28:54','2020-05-27 17:34:12',291),(41,'2020-05-27 18:10:29','2020-05-27 21:04:20',281),(51,'2020-05-27 21:46:09',NULL,281),(61,'2020-05-27 21:57:03','2020-05-27 22:06:08',281),(71,'2020-05-27 23:22:03','2020-05-27 23:22:32',281),(81,'2020-05-27 23:22:51','2020-05-27 23:23:05',301),(91,'2020-05-27 23:23:15',NULL,281);
/*!40000 ALTER TABLE `log_session` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payement`
--

DROP TABLE IF EXISTS `payement`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `payement` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `annee_expiration` int(11) NOT NULL,
  `cryptonyme_carte` int(11) NOT NULL,
  `mois_expiration` int(11) NOT NULL,
  `nom_proprietaire` varchar(255) DEFAULT NULL,
  `numero_carte` varchar(255) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK8s8q4ymfo3y7knm61do6o6pw` (`user_id`),
  CONSTRAINT `FK8s8q4ymfo3y7knm61do6o6pw` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payement`
--

LOCK TABLES `payement` WRITE;
/*!40000 ALTER TABLE `payement` DISABLE KEYS */;
INSERT INTO `payement` VALUES (1,5,654,1,'Stephane Martinize','0382738437484734',291);
/*!40000 ALTER TABLE `payement` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `profile`
--

DROP TABLE IF EXISTS `profile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `profile` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `libelle` varchar(255) DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `profile`
--

LOCK TABLES `profile` WRITE;
/*!40000 ALTER TABLE `profile` DISABLE KEYS */;
INSERT INTO `profile` VALUES (1,'parent','ROLE_PARENT'),(2,'eleve','ROLE_ELEVE');
/*!40000 ALTER TABLE `profile` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qcm`
--

DROP TABLE IF EXISTS `qcm`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qcm` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `listchoices` varchar(255) DEFAULT NULL,
  `question` varchar(255) DEFAULT NULL,
  `response` varchar(255) DEFAULT NULL,
  `exercice_id` int(11) DEFAULT NULL,
  `libelle` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKhxqh5yj3xwcth02qi1scimfg4` (`exercice_id`),
  CONSTRAINT `FKhxqh5yj3xwcth02qi1scimfg4` FOREIGN KEY (`exercice_id`) REFERENCES `exercice` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qcm`
--

LOCK TABLES `qcm` WRITE;
/*!40000 ALTER TABLE `qcm` DISABLE KEYS */;
INSERT INTO `qcm` VALUES (1,NULL,'Combien vaut 3+5²','B',1,'calcul puissance');
/*!40000 ALTER TABLE `qcm` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qr`
--

DROP TABLE IF EXISTS `qr`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qr` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `question` varchar(255) DEFAULT NULL,
  `response` varchar(255) DEFAULT NULL,
  `exercice_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK16mgci74iipv2th2q8ji55v1k` (`exercice_id`),
  CONSTRAINT `FK16mgci74iipv2th2q8ji55v1k` FOREIGN KEY (`exercice_id`) REFERENCES `exercice` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qr`
--

LOCK TABLES `qr` WRITE;
/*!40000 ALTER TABLE `qr` DISABLE KEYS */;
/*!40000 ALTER TABLE `qr` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `recommendation`
--

DROP TABLE IF EXISTS `recommendation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `recommendation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `contenu` varchar(255) DEFAULT NULL,
  `date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `recommendation`
--

LOCK TABLES `recommendation` WRITE;
/*!40000 ALTER TABLE `recommendation` DISABLE KEYS */;
INSERT INTO `recommendation` VALUES (1,'Bossez bien, les examens approches','2020-05-15 23:35:08'),(2,'Revenez sur votre cours, soyez motivé','2020-05-15 23:35:53'),(3,'Ne baissez pas les bras.','2020-05-15 23:36:10'),(4,'Courage, il vous reste pas beaucoup.','2020-05-15 23:36:25'),(5,'La réussité vient au bout de l\'effort.','2020-05-15 23:37:49'),(6,'Soyez perséverant','2020-05-15 23:37:49'),(7,'Bravo, Vous êtes bien avancé.','2020-05-15 23:39:20');
/*!40000 ALTER TABLE `recommendation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tchat`
--

DROP TABLE IF EXISTS `tchat`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tchat` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `date_send` varchar(255) DEFAULT NULL,
  `message` varchar(255) DEFAULT NULL,
  `reveiver_id` int(11) DEFAULT NULL,
  `transmitter_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKq68imj4bwyg1uw03she05ymeo` (`reveiver_id`),
  KEY `FK3sor9xyl3blvn0kbd2im0wmsx` (`transmitter_id`),
  CONSTRAINT `FK3sor9xyl3blvn0kbd2im0wmsx` FOREIGN KEY (`transmitter_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKq68imj4bwyg1uw03she05ymeo` FOREIGN KEY (`reveiver_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tchat`
--

LOCK TABLES `tchat` WRITE;
/*!40000 ALTER TABLE `tchat` DISABLE KEYS */;
/*!40000 ALTER TABLE `tchat` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(255) DEFAULT NULL,
  `firstname` varchar(255) DEFAULT NULL,
  `lastname` varchar(255) DEFAULT NULL,
  `level` varchar(255) DEFAULT NULL,
  `parent_relation` varchar(255) DEFAULT NULL,
  `sex` varchar(255) DEFAULT NULL,
  `tel` varchar(255) DEFAULT NULL,
  `ville` varchar(255) DEFAULT NULL,
  `compte_id` int(11) DEFAULT NULL,
  `parent_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK55wqyp3yo78bxyge0cuswgjeg` (`compte_id`),
  KEY `FK4k8a1qa0wofm01aijepmu0d4g` (`parent_id`),
  CONSTRAINT `FK4k8a1qa0wofm01aijepmu0d4g` FOREIGN KEY (`parent_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FK55wqyp3yo78bxyge0cuswgjeg` FOREIGN KEY (`compte_id`) REFERENCES `compte` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=311 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (112,'karim.dahdouh.fr@gmail.com','karim','dahdouh','null','null','null','0789876543','Montpellier',86,NULL),(120,'null','Yanis','Dubois','Cours élémentaire première année (CE1)','Mother','boy','null','null',94,112),(121,'null','Sebasiane','Martin','Cours élémentaire deuxième année (CE2)','Father','boy','null','null',95,112),(281,'karim.dahdouh.fr@gmail.com','Stephane','Martinize','null','null','null','0654345645','Montpellier',251,NULL),(291,'null','Adam','Martinize','Cours moyen première année (CM1)','Mère','garçon','null','null',261,281),(301,'null','Juliette','Martinize','Cours préparatoire (CP)','Mère','fille','null','null',271,281);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-05-28  1:26:09
