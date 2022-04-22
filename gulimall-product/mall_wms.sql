/*
 Navicat MySQL Data Transfer

 Source Server         : 192.168.247.130
 Source Server Type    : MySQL
 Source Server Version : 50736
 Source Host           : 192.168.247.130:3306
 Source Schema         : mall_wms

 Target Server Type    : MySQL
 Target Server Version : 50736
 File Encoding         : 65001

 Date: 23/04/2022 02:54:02
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for undo_log
-- ----------------------------
DROP TABLE IF EXISTS `undo_log`;
CREATE TABLE `undo_log`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `branch_id` bigint(20) NOT NULL,
  `xid` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `context` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `rollback_info` longblob NOT NULL,
  `log_status` int(11) NOT NULL,
  `log_created` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `log_modified` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `ext` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `ux_undo_log`(`xid`, `branch_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of undo_log
-- ----------------------------
INSERT INTO `undo_log` VALUES (7, 235535481539670016, '169.254.230.195:8091:235535479048253440', 'serializer=kryo', 0x2D01000000, 1, '2022-02-11 14:53:56', '2022-02-11 14:53:56', NULL);
INSERT INTO `undo_log` VALUES (10, 235538052442501120, '169.254.230.195:8091:235538043647045632', 'serializer=kryo', 0x2D01000000, 1, '2022-02-11 15:04:09', '2022-02-11 15:04:09', NULL);
INSERT INTO `undo_log` VALUES (13, 235539049738940416, '169.254.230.195:8091:235539045632716800', 'serializer=kryo', 0x2D01000000, 1, '2022-02-11 15:08:06', '2022-02-11 15:08:06', NULL);
INSERT INTO `undo_log` VALUES (16, 235540292939034624, '169.254.230.195:8091:235540289264824320', 'serializer=kryo', 0x2D01000000, 1, '2022-02-11 15:13:03', '2022-02-11 15:13:03', NULL);

-- ----------------------------
-- Table structure for wms_purchase
-- ----------------------------
DROP TABLE IF EXISTS `wms_purchase`;
CREATE TABLE `wms_purchase`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `assignee_id` bigint(20) NULL DEFAULT NULL,
  `assignee_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `phone` char(13) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `priority` int(4) NULL DEFAULT NULL,
  `status` int(4) NULL DEFAULT NULL,
  `ware_id` bigint(20) NULL DEFAULT NULL,
  `amount` decimal(18, 4) NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '采购信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of wms_purchase
-- ----------------------------
INSERT INTO `wms_purchase` VALUES (7, 2, 'liming', '16621735515', 2, 3, NULL, NULL, '2021-12-19 03:55:35', '2021-12-19 04:04:09');

-- ----------------------------
-- Table structure for wms_purchase_detail
-- ----------------------------
DROP TABLE IF EXISTS `wms_purchase_detail`;
CREATE TABLE `wms_purchase_detail`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `purchase_id` bigint(20) NULL DEFAULT NULL COMMENT '采购单id',
  `sku_id` bigint(20) NULL DEFAULT NULL COMMENT '采购商品id',
  `sku_num` int(11) NULL DEFAULT NULL COMMENT '采购数量',
  `sku_price` decimal(18, 4) NULL DEFAULT NULL COMMENT '采购金额',
  `ware_id` bigint(20) NULL DEFAULT NULL COMMENT '仓库id',
  `status` int(11) NULL DEFAULT NULL COMMENT '状态[0新建，1已分配，2正在采购，3已完成，4采购失败]',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 32 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of wms_purchase_detail
-- ----------------------------
INSERT INTO `wms_purchase_detail` VALUES (19, 7, 13, 10, NULL, 1, 3);
INSERT INTO `wms_purchase_detail` VALUES (20, 7, 14, 50, NULL, 1, 3);
INSERT INTO `wms_purchase_detail` VALUES (21, 7, 15, 50, NULL, 1, 3);
INSERT INTO `wms_purchase_detail` VALUES (22, 7, 16, 50, NULL, 1, 3);
INSERT INTO `wms_purchase_detail` VALUES (23, 7, 17, 10, NULL, 1, 3);
INSERT INTO `wms_purchase_detail` VALUES (24, 7, 18, 10, NULL, 1, 3);
INSERT INTO `wms_purchase_detail` VALUES (25, 7, 19, 20, NULL, 1, 3);
INSERT INTO `wms_purchase_detail` VALUES (26, 7, 20, 20, NULL, 1, 3);
INSERT INTO `wms_purchase_detail` VALUES (27, 7, 21, 20, NULL, 1, 3);
INSERT INTO `wms_purchase_detail` VALUES (28, 7, 22, 20, NULL, 1, 3);
INSERT INTO `wms_purchase_detail` VALUES (29, 7, 23, 10, NULL, 1, 3);
INSERT INTO `wms_purchase_detail` VALUES (30, 7, 24, 10, NULL, 1, 3);
INSERT INTO `wms_purchase_detail` VALUES (31, NULL, 13, 10, NULL, 1, 0);

-- ----------------------------
-- Table structure for wms_ware_info
-- ----------------------------
DROP TABLE IF EXISTS `wms_ware_info`;
CREATE TABLE `wms_ware_info`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '仓库名',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '仓库地址',
  `areacode` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '区域编码',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '仓库信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of wms_ware_info
-- ----------------------------
INSERT INTO `wms_ware_info` VALUES (1, '1号仓库', '上海市虹口区', '000000');
INSERT INTO `wms_ware_info` VALUES (2, '2号仓库', '北京市', '111111');

-- ----------------------------
-- Table structure for wms_ware_order_task
-- ----------------------------
DROP TABLE IF EXISTS `wms_ware_order_task`;
CREATE TABLE `wms_ware_order_task`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `order_id` bigint(20) NULL DEFAULT NULL COMMENT 'order_id',
  `order_sn` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'order_sn',
  `consignee` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '收货人',
  `consignee_tel` char(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '收货人电话',
  `delivery_address` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '配送地址',
  `order_comment` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '订单备注',
  `payment_way` tinyint(1) NULL DEFAULT NULL COMMENT '付款方式【 1:在线付款 2:货到付款】',
  `task_status` tinyint(2) NULL DEFAULT NULL COMMENT '任务状态',
  `order_body` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '订单描述',
  `tracking_no` char(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '物流单号',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT 'create_time',
  `ware_id` bigint(20) NULL DEFAULT NULL COMMENT '仓库id',
  `task_comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '工作单备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 43 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '库存工作单' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of wms_ware_order_task
-- ----------------------------
INSERT INTO `wms_ware_order_task` VALUES (1, NULL, '202202130024107071492535008789901313', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (2, NULL, '202202130033400701492537396829466626', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (3, NULL, '202202130040040431492539007400259585', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (4, NULL, '202202130050539211492541733131628545', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (5, NULL, '202202130054338871492542655752679426', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (6, NULL, '202202130056189841492543096540475393', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (7, NULL, '202202130057063471492543295191101442', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (8, NULL, '202202130116496821492548258503868417', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (9, NULL, '202202130118200781492548637618618370', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (10, NULL, '202202130122173611492549632855654402', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (11, NULL, '202202130131054221492551847704018945', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (12, NULL, '202202131705357331492787023658913793', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (13, NULL, '202202131713274761492789002279882754', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (14, NULL, '202202131804376581492801879569108994', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (15, NULL, '202202131820019251492805756221726721', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (16, NULL, '202202131826384881492807419514920962', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (17, NULL, '202202131835084481492809558433800194', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (18, NULL, '202202131837301091492810152544378881', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (19, NULL, '202204191325338611516286862268866561', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (20, NULL, '202204191331440411516288414907899905', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (23, NULL, '202204191337320561516289874563485698', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (24, NULL, '202204191340399551516290662706769922', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (25, NULL, '202204191344241541516291603052961793', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (26, NULL, '202204191353191341516293846913318913', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (27, NULL, '202204191356557671516294755546353665', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (28, NULL, '202204211655480571517064545764466689', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (29, NULL, '202204211707298811517067489423482882', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (30, NULL, '202204221319209551517372461782495233', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (31, NULL, '202204221328175801517374712538210306', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (32, NULL, '202204221342129601517378216354914306', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (33, NULL, '202204221405390451517384113928019970', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (34, NULL, '202204221407028261517384465301643266', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (35, NULL, '202204221840552251517453387923263490', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (36, NULL, '202204221846154931517454731098746882', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (37, NULL, '202204221850062771517455699081183233', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (38, NULL, '202204221857195281517457516267589634', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (39, NULL, '202204221927495661517465192007495682', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (40, NULL, '202204221950000811517470772591874049', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (41, NULL, '202204221954508681517471992240963586', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (42, NULL, '202204222001055861517473563922169857', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for wms_ware_order_task_detail
-- ----------------------------
DROP TABLE IF EXISTS `wms_ware_order_task_detail`;
CREATE TABLE `wms_ware_order_task_detail`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `sku_id` bigint(20) NULL DEFAULT NULL COMMENT 'sku_id',
  `sku_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'sku_name',
  `sku_num` int(11) NULL DEFAULT NULL COMMENT '购买个数',
  `task_id` bigint(20) NULL DEFAULT NULL COMMENT '工作单id',
  `ware_id` bigint(20) NULL DEFAULT NULL COMMENT '仓库id',
  `lock_status` int(1) NULL DEFAULT NULL COMMENT '1-已锁定  2-已解锁  3-扣减',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 51 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '库存工作单' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of wms_ware_order_task_detail
-- ----------------------------
INSERT INTO `wms_ware_order_task_detail` VALUES (10, 13, '', 2, 10, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (11, 13, '', 2, 11, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (12, 13, '', 2, 12, 1, 1);
INSERT INTO `wms_ware_order_task_detail` VALUES (13, 13, '', 3, 13, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (14, 13, '', 2, 14, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (15, 13, '', 3, 15, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (16, 13, '', 2, 16, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (17, 13, '', 2, 17, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (18, 13, '', 3, 18, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (19, 13, '', 6, 19, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (20, 14, '', 1, 20, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (21, 13, '', 6, 20, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (22, 14, '', 1, 23, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (23, 13, '', 1, 23, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (24, 14, '', 1, 24, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (25, 13, '', 1, 24, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (26, 14, '', 1, 25, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (27, 13, '', 1, 25, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (28, 14, '', 1, 26, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (29, 13, '', 2, 26, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (30, 14, '', 1, 27, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (31, 13, '', 1, 27, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (32, 14, '', 1, 28, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (33, 13, '', 1, 28, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (34, 14, '', 1, 29, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (35, 13, '', 1, 29, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (36, 14, '', 1, 30, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (37, 13, '', 1, 30, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (38, 14, '', 1, 31, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (39, 14, '', 2, 32, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (40, 16, '', 2, 33, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (41, 14, '', 2, 33, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (42, 14, '', 1, 34, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (43, 13, '', 1, 35, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (44, 14, '', 1, 36, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (45, 14, '', 1, 37, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (46, 14, '', 2, 38, 1, 1);
INSERT INTO `wms_ware_order_task_detail` VALUES (47, 13, '', 1, 39, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (48, 14, '', 1, 40, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (49, 14, '', 1, 41, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (50, 14, '', 1, 42, 1, 2);

-- ----------------------------
-- Table structure for wms_ware_sku
-- ----------------------------
DROP TABLE IF EXISTS `wms_ware_sku`;
CREATE TABLE `wms_ware_sku`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `sku_id` bigint(20) NULL DEFAULT NULL COMMENT 'sku_id',
  `ware_id` bigint(20) NULL DEFAULT NULL COMMENT '仓库id',
  `stock` int(11) NULL DEFAULT NULL COMMENT '库存数',
  `sku_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'sku_name',
  `stock_locked` int(11) NULL DEFAULT 0 COMMENT '锁定库存',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `sku_id`(`sku_id`) USING BTREE,
  INDEX `ware_id`(`ware_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品库存' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of wms_ware_sku
-- ----------------------------
INSERT INTO `wms_ware_sku` VALUES (2, 13, 1, 20, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB 亮黑色 5G全网通8GB+128', 0);
INSERT INTO `wms_ware_sku` VALUES (3, 14, 1, 100, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB 亮黑色 5G全网通8GB+256GB', 2);
INSERT INTO `wms_ware_sku` VALUES (4, 15, 1, 100, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB 亮黑色 4G全网通8GB+256GB', 0);
INSERT INTO `wms_ware_sku` VALUES (5, 16, 1, 100, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB 亮黑色 4G全网通8GB+128GB', 0);
INSERT INTO `wms_ware_sku` VALUES (6, 17, 1, 20, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB 星河银 5G全网通8GB+128', 0);
INSERT INTO `wms_ware_sku` VALUES (7, 18, 1, 20, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB 星河银 5G全网通8GB+256GB', 0);
INSERT INTO `wms_ware_sku` VALUES (8, 19, 1, 40, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB 星河银 4G全网通8GB+256GB', 0);
INSERT INTO `wms_ware_sku` VALUES (9, 20, 1, 40, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB 星河银 4G全网通8GB+128GB', 0);
INSERT INTO `wms_ware_sku` VALUES (10, 21, 1, 40, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB 翡冷翠 5G全网通8GB+128', 0);
INSERT INTO `wms_ware_sku` VALUES (11, 22, 1, 40, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB 翡冷翠 5G全网通8GB+256GB', 0);
INSERT INTO `wms_ware_sku` VALUES (12, 23, 1, 20, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB 翡冷翠 4G全网通8GB+256GB', 0);
INSERT INTO `wms_ware_sku` VALUES (13, 24, 1, 20, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB 翡冷翠 4G全网通8GB+128GB', 0);

SET FOREIGN_KEY_CHECKS = 1;
