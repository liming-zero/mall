/*
 Navicat MySQL Data Transfer

 Source Server         : 192.168.247.130
 Source Server Type    : MySQL
 Source Server Version : 50736
 Source Host           : 192.168.247.130:3306
 Source Schema         : mall_oms

 Target Server Type    : MySQL
 Target Server Version : 50736
 File Encoding         : 65001

 Date: 23/04/2022 02:51:04
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for mq_message
-- ----------------------------
DROP TABLE IF EXISTS `mq_message`;
CREATE TABLE `mq_message`  (
  `message_id` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `to_exchane` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `routing_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `class_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `message_status` int(1) NULL DEFAULT 0 COMMENT '0-新建 1-已发送 2-错误抵达 3-已抵达',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`message_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mq_message
-- ----------------------------

-- ----------------------------
-- Table structure for oms_order
-- ----------------------------
DROP TABLE IF EXISTS `oms_order`;
CREATE TABLE `oms_order`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `member_id` bigint(20) NULL DEFAULT NULL COMMENT 'member_id',
  `order_sn` char(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '唯一订单号',
  `coupon_id` bigint(20) NULL DEFAULT NULL COMMENT 'ʹ�õ��Ż�ȯ',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT 'create_time',
  `member_username` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '�û���',
  `total_amount` decimal(18, 4) NULL DEFAULT NULL COMMENT '�����ܶ�',
  `pay_amount` decimal(18, 4) NULL DEFAULT NULL COMMENT 'Ӧ���ܶ�',
  `freight_amount` decimal(18, 4) NULL DEFAULT NULL COMMENT '�˷ѽ��',
  `promotion_amount` decimal(18, 4) NULL DEFAULT NULL COMMENT '�����Ż��������ۡ����������ݼۣ�',
  `integration_amount` decimal(18, 4) NULL DEFAULT NULL COMMENT '���ֵֿ۽��',
  `coupon_amount` decimal(18, 4) NULL DEFAULT NULL COMMENT '�Ż�ȯ�ֿ۽��',
  `discount_amount` decimal(18, 4) NULL DEFAULT NULL COMMENT '��̨��������ʹ�õ��ۿ۽��',
  `pay_type` tinyint(4) NULL DEFAULT NULL COMMENT '֧����ʽ��1->֧������2->΢�ţ�3->������ 4->���������',
  `source_type` tinyint(4) NULL DEFAULT NULL COMMENT '������Դ[0->PC������1->app����]',
  `status` tinyint(4) NULL DEFAULT NULL COMMENT '����״̬��0->�����1->��������2->�ѷ�����3->����ɣ�4->�ѹرգ�5->��Ч������',
  `delivery_company` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '������˾(���ͷ�ʽ)',
  `delivery_sn` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '��������',
  `auto_confirm_day` int(11) NULL DEFAULT NULL COMMENT '�Զ�ȷ��ʱ�䣨�죩',
  `integration` int(11) NULL DEFAULT NULL COMMENT '���Ի�õĻ���',
  `growth` int(11) NULL DEFAULT NULL COMMENT '���Ի�õĳɳ�ֵ',
  `bill_type` tinyint(4) NULL DEFAULT NULL COMMENT '��Ʊ����[0->������Ʊ��1->���ӷ�Ʊ��2->ֽ�ʷ�Ʊ]',
  `bill_header` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '��Ʊ̧ͷ',
  `bill_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '��Ʊ����',
  `bill_receiver_phone` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '��Ʊ�˵绰',
  `bill_receiver_email` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '��Ʊ������',
  `receiver_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '�ջ�������',
  `receiver_phone` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '�ջ��˵绰',
  `receiver_post_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '�ջ����ʱ�',
  `receiver_province` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ʡ��/ֱϽ��',
  `receiver_city` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '����',
  `receiver_region` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '��',
  `receiver_detail_address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '��ϸ��ַ',
  `note` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '������ע',
  `confirm_status` tinyint(4) NULL DEFAULT NULL COMMENT 'ȷ���ջ�״̬[0->δȷ�ϣ�1->��ȷ��]',
  `delete_status` tinyint(4) NULL DEFAULT NULL COMMENT 'ɾ��״̬��0->δɾ����1->��ɾ����',
  `use_integration` int(11) NULL DEFAULT NULL COMMENT '�µ�ʱʹ�õĻ���',
  `payment_time` datetime(0) NULL DEFAULT NULL COMMENT '֧��ʱ��',
  `delivery_time` datetime(0) NULL DEFAULT NULL COMMENT '����ʱ��',
  `receive_time` datetime(0) NULL DEFAULT NULL COMMENT 'ȷ���ջ�ʱ��',
  `comment_time` datetime(0) NULL DEFAULT NULL COMMENT '����ʱ��',
  `modify_time` datetime(0) NULL DEFAULT NULL COMMENT '�޸�ʱ��',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `order_sn`(`order_sn`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 32 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '����' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of oms_order
-- ----------------------------
INSERT INTO `oms_order` VALUES (1, 6, '202202131705357331492787023658913793', NULL, '2022-02-13 09:05:36', 'LM91699', 11998.0000, 12003.0000, 5.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 11998, 11998, NULL, NULL, NULL, NULL, NULL, '李明', '16621735515', NULL, '上海市', NULL, NULL, '上海市虹口区虹湾路', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-02-13 09:05:36');
INSERT INTO `oms_order` VALUES (2, 6, '202202131713274761492789002279882754', NULL, '2022-02-13 09:13:28', 'LM91699', 17997.0000, 18002.0000, 5.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 17997, 17997, NULL, NULL, NULL, NULL, NULL, '李明', '16621735515', NULL, '上海市', NULL, NULL, '上海市虹口区虹湾路', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-02-13 09:13:28');
INSERT INTO `oms_order` VALUES (3, 6, '202202131804376581492801879569108994', NULL, '2022-02-13 10:04:38', 'LM91699', 11998.0000, 12003.0000, 5.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 11998, 11998, NULL, NULL, NULL, NULL, NULL, '李明', '16621735515', NULL, '上海市', NULL, NULL, '上海市虹口区虹湾路', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-02-13 10:04:38');
INSERT INTO `oms_order` VALUES (4, 6, '202202131820019251492805756221726721', NULL, '2022-02-13 10:20:02', 'LM91699', 17997.0000, 18002.0000, 5.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 17997, 17997, NULL, NULL, NULL, NULL, NULL, '李明', '16621735515', NULL, '上海市', NULL, NULL, '上海市虹口区虹湾路', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-02-13 10:20:02');
INSERT INTO `oms_order` VALUES (5, 6, '202202131826384881492807419514920962', NULL, '2022-02-13 10:26:39', 'LM91699', 11998.0000, 12003.0000, 5.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 11998, 11998, NULL, NULL, NULL, NULL, NULL, '李明', '16621735515', NULL, '上海市', NULL, NULL, '上海市虹口区虹湾路', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-02-13 10:26:39');
INSERT INTO `oms_order` VALUES (6, 6, '202202131835084481492809558433800194', NULL, '2022-02-13 10:35:09', 'LM91699', 11998.0000, 12003.0000, 5.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 11998, 11998, NULL, NULL, NULL, NULL, NULL, '李明', '16621735515', NULL, '上海市', NULL, NULL, '上海市虹口区虹湾路', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-02-13 10:35:09');
INSERT INTO `oms_order` VALUES (7, 6, '202202131837301091492810152544378881', NULL, '2022-02-13 10:37:30', 'LM91699', 17997.0000, 18002.0000, 5.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 17997, 17997, NULL, NULL, NULL, NULL, NULL, '李明', '16621735515', NULL, '上海市', NULL, NULL, '上海市虹口区虹湾路', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-02-13 10:37:30');
INSERT INTO `oms_order` VALUES (8, 6, '202204191325338611516286862268866561', NULL, '2022-04-19 05:25:34', 'LM91699', 35994.0000, 35999.0000, 5.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 35994, 35994, NULL, NULL, NULL, NULL, NULL, '李明', '16621735515', NULL, '上海市', NULL, NULL, '上海市虹口区虹湾路', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-04-19 05:25:34');
INSERT INTO `oms_order` VALUES (9, 6, '202204191331440411516288414907899905', NULL, '2022-04-19 05:31:44', 'LM91699', 42293.0000, 42298.0000, 5.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 42293, 42293, NULL, NULL, NULL, NULL, NULL, '李明', '16621735515', NULL, '上海市', NULL, NULL, '上海市虹口区虹湾路', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-04-19 05:31:44');
INSERT INTO `oms_order` VALUES (12, 6, '202204191337320561516289874563485698', NULL, '2022-04-19 05:37:32', 'LM91699', 12298.0000, 12303.0000, 5.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 12298, 12298, NULL, NULL, NULL, NULL, NULL, '李明', '16621735515', NULL, '上海市', NULL, NULL, '上海市虹口区虹湾路', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-04-19 05:37:32');
INSERT INTO `oms_order` VALUES (13, 6, '202204191340399551516290662706769922', NULL, '2022-04-19 05:40:40', 'LM91699', 12298.0000, 12303.0000, 5.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 12298, 12298, NULL, NULL, NULL, NULL, NULL, '李明', '16621735515', NULL, '上海市', NULL, NULL, '上海市虹口区虹湾路', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-04-19 05:40:40');
INSERT INTO `oms_order` VALUES (14, 6, '202204191344241541516291603052961793', NULL, '2022-04-19 05:44:25', 'LM91699', 12298.0000, 12303.0000, 5.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 12298, 12298, NULL, NULL, NULL, NULL, NULL, '李明', '16621735515', NULL, '上海市', NULL, NULL, '上海市虹口区虹湾路', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-04-19 05:44:25');
INSERT INTO `oms_order` VALUES (15, 6, '202204191353191341516293846913318913', NULL, '2022-04-19 05:53:19', 'LM91699', 18297.0000, 18302.0000, 5.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 18297, 18297, NULL, NULL, NULL, NULL, NULL, '李明', '16621735515', NULL, '上海市', NULL, NULL, '上海市虹口区虹湾路', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-04-19 05:53:19');
INSERT INTO `oms_order` VALUES (16, 6, '202204191356557671516294755546353665', NULL, '2022-04-19 05:56:56', 'LM91699', 12298.0000, 12303.0000, 5.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 12298, 12298, NULL, NULL, NULL, NULL, NULL, '李明', '16621735515', NULL, '上海市', NULL, NULL, '上海市虹口区虹湾路', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-04-19 05:56:56');
INSERT INTO `oms_order` VALUES (17, 6, '202204211655480571517064545764466689', NULL, '2022-04-21 08:55:48', 'LM91699', 12298.0000, 12303.0000, 5.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 12298, 12298, NULL, NULL, NULL, NULL, NULL, '李明', '16621735515', NULL, '上海市', NULL, NULL, '上海市虹口区虹湾路', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-04-21 08:55:48');
INSERT INTO `oms_order` VALUES (18, 6, '202204211707298811517067489423482882', NULL, '2022-04-21 09:07:30', 'LM91699', 12298.0000, 12303.0000, 5.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 12298, 12298, NULL, NULL, NULL, NULL, NULL, '李明', '16621735515', NULL, '上海市', NULL, NULL, '上海市虹口区虹湾路', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-04-21 09:07:30');
INSERT INTO `oms_order` VALUES (19, 6, '202204221319209551517372461782495233', NULL, '2022-04-22 05:19:21', 'LM91699', 12298.0000, 12303.0000, 5.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 12298, 12298, NULL, NULL, NULL, NULL, NULL, '李明', '16621735515', NULL, '上海市', NULL, NULL, '上海市虹口区虹湾路', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-04-22 05:19:21');
INSERT INTO `oms_order` VALUES (20, 6, '202204221328175801517374712538210306', NULL, '2022-04-22 05:28:18', 'LM91699', 6299.0000, 6304.0000, 5.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 6299, 6299, NULL, NULL, NULL, NULL, NULL, '李明', '16621735515', NULL, '上海市', NULL, NULL, '上海市虹口区虹湾路', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-04-22 05:28:18');
INSERT INTO `oms_order` VALUES (21, 6, '202204221342129601517378216354914306', NULL, '2022-04-22 05:42:13', 'LM91699', 12598.0000, 12603.0000, 5.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 12598, 12598, NULL, NULL, NULL, NULL, NULL, '李明', '16621735515', NULL, '上海市', NULL, NULL, '上海市虹口区虹湾路', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-04-22 05:42:13');
INSERT INTO `oms_order` VALUES (22, 6, '202204221405390451517384113928019970', NULL, '2022-04-22 06:05:39', 'LM91699', 22596.0000, 22601.0000, 5.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 22596, 22596, NULL, NULL, NULL, NULL, NULL, '李明', '16621735515', NULL, '上海市', NULL, NULL, '上海市虹口区虹湾路', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-04-22 06:05:39');
INSERT INTO `oms_order` VALUES (23, 6, '202204221407028261517384465301643266', NULL, '2022-04-22 06:07:03', 'LM91699', 6299.0000, 6304.0000, 5.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 6299, 6299, NULL, NULL, NULL, NULL, NULL, '李明', '16621735515', NULL, '上海市', NULL, NULL, '上海市虹口区虹湾路', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-04-22 06:07:03');
INSERT INTO `oms_order` VALUES (24, 6, '202204221840552251517453387923263490', NULL, '2022-04-22 10:40:56', 'LM91699', 5999.0000, 6004.0000, 5.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 5999, 5999, NULL, NULL, NULL, NULL, NULL, '李明', '16621735515', NULL, '上海市', NULL, NULL, '上海市虹口区虹湾路', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-04-22 10:40:56');
INSERT INTO `oms_order` VALUES (25, 6, '202204221846154931517454731098746882', NULL, '2022-04-22 10:46:16', 'LM91699', 6299.0000, 6304.0000, 5.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 6299, 6299, NULL, NULL, NULL, NULL, NULL, '李明', '16621735515', NULL, '上海市', NULL, NULL, '上海市虹口区虹湾路', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-04-22 10:46:16');
INSERT INTO `oms_order` VALUES (26, 6, '202204221850062771517455699081183233', NULL, '2022-04-22 10:50:07', 'LM91699', 6299.0000, 6304.0000, 5.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 6299, 6299, NULL, NULL, NULL, NULL, NULL, '李明', '16621735515', NULL, '上海市', NULL, NULL, '上海市虹口区虹湾路', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-04-22 10:50:07');
INSERT INTO `oms_order` VALUES (27, 6, '202204221857195281517457516267589634', NULL, '2022-04-22 10:57:20', 'LM91699', 12598.0000, 12603.0000, 5.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 1, NULL, NULL, 7, 12598, 12598, NULL, NULL, NULL, NULL, NULL, '李明', '16621735515', NULL, '上海市', NULL, NULL, '上海市虹口区虹湾路', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-04-22 10:57:20');
INSERT INTO `oms_order` VALUES (28, 6, '202204221927495661517465192007495682', NULL, '2022-04-22 11:27:50', 'LM91699', 5999.0000, 6004.0000, 5.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 5999, 5999, NULL, NULL, NULL, NULL, NULL, '李明', '16621735515', NULL, '上海市', NULL, NULL, '上海市虹口区虹湾路', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-04-22 11:27:50');
INSERT INTO `oms_order` VALUES (29, 6, '202204221950000811517470772591874049', NULL, '2022-04-22 11:50:00', 'LM91699', 6299.0000, 6304.0000, 5.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 1, NULL, NULL, 7, 6299, 6299, NULL, NULL, NULL, NULL, NULL, '李明', '16621735515', NULL, '上海市', NULL, NULL, '上海市虹口区虹湾路', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-04-22 11:50:00');
INSERT INTO `oms_order` VALUES (30, 6, '202204221954508681517471992240963586', NULL, '2022-04-22 11:54:51', 'LM91699', 6299.0000, 6304.0000, 5.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 6299, 6299, NULL, NULL, NULL, NULL, NULL, '李明', '16621735515', NULL, '上海市', NULL, NULL, '上海市虹口区虹湾路', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-04-22 11:54:51');
INSERT INTO `oms_order` VALUES (31, 6, '202204222001055861517473563922169857', NULL, '2022-04-22 12:01:06', 'LM91699', 6299.0000, 6304.0000, 5.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 6299, 6299, NULL, NULL, NULL, NULL, NULL, '李明', '16621735515', NULL, '上海市', NULL, NULL, '上海市虹口区虹湾路', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-04-22 12:01:06');

-- ----------------------------
-- Table structure for oms_order_item
-- ----------------------------
DROP TABLE IF EXISTS `oms_order_item`;
CREATE TABLE `oms_order_item`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `order_id` bigint(20) NULL DEFAULT NULL COMMENT 'order_id',
  `order_sn` char(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'order_sn',
  `spu_id` bigint(20) NULL DEFAULT NULL COMMENT 'spu_id',
  `spu_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'spu_name',
  `spu_pic` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'spu_pic',
  `spu_brand` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Ʒ��',
  `category_id` bigint(20) NULL DEFAULT NULL COMMENT '��Ʒ����id',
  `sku_id` bigint(20) NULL DEFAULT NULL COMMENT '��Ʒsku���',
  `sku_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '��Ʒsku����',
  `sku_pic` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '��ƷskuͼƬ',
  `sku_price` decimal(18, 4) NULL DEFAULT NULL COMMENT '��Ʒsku�۸�',
  `sku_quantity` int(11) NULL DEFAULT NULL COMMENT '��Ʒ���������',
  `sku_attrs_vals` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '��Ʒ����������ϣ�JSON��',
  `promotion_amount` decimal(18, 4) NULL DEFAULT NULL COMMENT '��Ʒ�����ֽ���',
  `coupon_amount` decimal(18, 4) NULL DEFAULT NULL COMMENT '�Ż�ȯ�Żݷֽ���',
  `integration_amount` decimal(18, 4) NULL DEFAULT NULL COMMENT '�����Żݷֽ���',
  `real_amount` decimal(18, 4) NULL DEFAULT NULL COMMENT '����Ʒ�����Żݺ�ķֽ���',
  `gift_integration` int(11) NULL DEFAULT NULL COMMENT '���ͻ���',
  `gift_growth` int(11) NULL DEFAULT NULL COMMENT '���ͳɳ�ֵ',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 55 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '��������Ϣ' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of oms_order_item
-- ----------------------------
INSERT INTO `oms_order_item` VALUES (9, NULL, '202202062219192111490329259955232770', 3, '?? HUAWEI Mate 30 Pro 5G ??990 OLED????4000??????? ??? 4G???8GB+256GB', NULL, '2', 225, 13, '?? HUAWEI Mate 30 Pro 5G ??990 OLED????4000??????? ??? 4G???8GB+256GB ??? 5G???8GB+128', 'https://lm--gulimall.oss-cn-shanghai.aliyuncs.com/2021-12-19//8e2b617e-2216-4302-89da-2a3dd56cb0b6_28f296629cca865e.jpg', 5999.0000, 1, '??:???;??:5G???8GB+128', 0.0000, 0.0000, 0.0000, 5999.0000, 5999, 5999);
INSERT INTO `oms_order_item` VALUES (10, NULL, '202202131705357331492787023658913793', 3, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB', NULL, '2', 225, 13, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB 亮黑色 5G全网通8GB+128', 'https://lm--gulimall.oss-cn-shanghai.aliyuncs.com/2021-12-19//8e2b617e-2216-4302-89da-2a3dd56cb0b6_28f296629cca865e.jpg', 5999.0000, 2, '颜色:亮黑色;版本:5G全网通8GB+128', 0.0000, 0.0000, 0.0000, 11998.0000, 11998, 11998);
INSERT INTO `oms_order_item` VALUES (11, NULL, '202202131713274761492789002279882754', 3, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB', NULL, '2', 225, 13, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB 亮黑色 5G全网通8GB+128', 'https://lm--gulimall.oss-cn-shanghai.aliyuncs.com/2021-12-19//8e2b617e-2216-4302-89da-2a3dd56cb0b6_28f296629cca865e.jpg', 5999.0000, 3, '颜色:亮黑色;版本:5G全网通8GB+128', 0.0000, 0.0000, 0.0000, 17997.0000, 17997, 17997);
INSERT INTO `oms_order_item` VALUES (12, NULL, '202202131804376581492801879569108994', 3, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB', NULL, '2', 225, 13, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB 亮黑色 5G全网通8GB+128', 'https://lm--gulimall.oss-cn-shanghai.aliyuncs.com/2021-12-19//8e2b617e-2216-4302-89da-2a3dd56cb0b6_28f296629cca865e.jpg', 5999.0000, 2, '颜色:亮黑色;版本:5G全网通8GB+128', 0.0000, 0.0000, 0.0000, 11998.0000, 11998, 11998);
INSERT INTO `oms_order_item` VALUES (13, NULL, '202202131820019251492805756221726721', 3, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB', NULL, '2', 225, 13, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB 亮黑色 5G全网通8GB+128', 'https://lm--gulimall.oss-cn-shanghai.aliyuncs.com/2021-12-19//8e2b617e-2216-4302-89da-2a3dd56cb0b6_28f296629cca865e.jpg', 5999.0000, 3, '颜色:亮黑色;版本:5G全网通8GB+128', 0.0000, 0.0000, 0.0000, 17997.0000, 17997, 17997);
INSERT INTO `oms_order_item` VALUES (14, NULL, '202202131826384881492807419514920962', 3, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB', NULL, '2', 225, 13, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB 亮黑色 5G全网通8GB+128', 'https://lm--gulimall.oss-cn-shanghai.aliyuncs.com/2021-12-19//8e2b617e-2216-4302-89da-2a3dd56cb0b6_28f296629cca865e.jpg', 5999.0000, 2, '颜色:亮黑色;版本:5G全网通8GB+128', 0.0000, 0.0000, 0.0000, 11998.0000, 11998, 11998);
INSERT INTO `oms_order_item` VALUES (15, NULL, '202202131835084481492809558433800194', 3, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB', NULL, '2', 225, 13, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB 亮黑色 5G全网通8GB+128', 'https://lm--gulimall.oss-cn-shanghai.aliyuncs.com/2021-12-19//8e2b617e-2216-4302-89da-2a3dd56cb0b6_28f296629cca865e.jpg', 5999.0000, 2, '颜色:亮黑色;版本:5G全网通8GB+128', 0.0000, 0.0000, 0.0000, 11998.0000, 11998, 11998);
INSERT INTO `oms_order_item` VALUES (16, NULL, '202202131837301091492810152544378881', 3, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB', NULL, '2', 225, 13, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB 亮黑色 5G全网通8GB+128', 'https://lm--gulimall.oss-cn-shanghai.aliyuncs.com/2021-12-19//8e2b617e-2216-4302-89da-2a3dd56cb0b6_28f296629cca865e.jpg', 5999.0000, 3, '颜色:亮黑色;版本:5G全网通8GB+128', 0.0000, 0.0000, 0.0000, 17997.0000, 17997, 17997);
INSERT INTO `oms_order_item` VALUES (17, NULL, '202204191325338611516286862268866561', 3, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB', NULL, '2', 225, 13, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB 亮黑色 5G全网通8GB+128', 'https://lm--gulimall.oss-cn-shanghai.aliyuncs.com/2021-12-19//8e2b617e-2216-4302-89da-2a3dd56cb0b6_28f296629cca865e.jpg', 5999.0000, 6, '颜色:亮黑色;版本:5G全网通8GB+128', 0.0000, 0.0000, 0.0000, 35994.0000, 35994, 35994);
INSERT INTO `oms_order_item` VALUES (18, NULL, '202204191331440411516288414907899905', 3, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB', NULL, '2', 225, 14, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB 亮黑色 5G全网通8GB+256GB', 'https://lm--gulimall.oss-cn-shanghai.aliyuncs.com/2021-12-19//3508e663-03cf-481c-8c9c-b95c5139a654_8bf441260bffa42f.jpg', 6299.0000, 1, '颜色:亮黑色;版本:5G全网通8GB+256GB', 0.0000, 0.0000, 0.0000, 6299.0000, 6299, 6299);
INSERT INTO `oms_order_item` VALUES (19, NULL, '202204191331440411516288414907899905', 3, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB', NULL, '2', 225, 13, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB 亮黑色 5G全网通8GB+128', 'https://lm--gulimall.oss-cn-shanghai.aliyuncs.com/2021-12-19//8e2b617e-2216-4302-89da-2a3dd56cb0b6_28f296629cca865e.jpg', 5999.0000, 6, '颜色:亮黑色;版本:5G全网通8GB+128', 0.0000, 0.0000, 0.0000, 35994.0000, 35994, 35994);
INSERT INTO `oms_order_item` VALUES (26, NULL, '202204191337320561516289874563485698', 3, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB', NULL, '2', 225, 14, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB 亮黑色 5G全网通8GB+256GB', 'https://lm--gulimall.oss-cn-shanghai.aliyuncs.com/2021-12-19//3508e663-03cf-481c-8c9c-b95c5139a654_8bf441260bffa42f.jpg', 6299.0000, 1, '颜色:亮黑色;版本:5G全网通8GB+256GB', 0.0000, 0.0000, 0.0000, 6299.0000, 6299, 6299);
INSERT INTO `oms_order_item` VALUES (27, NULL, '202204191337320561516289874563485698', 3, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB', NULL, '2', 225, 13, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB 亮黑色 5G全网通8GB+128', 'https://lm--gulimall.oss-cn-shanghai.aliyuncs.com/2021-12-19//8e2b617e-2216-4302-89da-2a3dd56cb0b6_28f296629cca865e.jpg', 5999.0000, 1, '颜色:亮黑色;版本:5G全网通8GB+128', 0.0000, 0.0000, 0.0000, 5999.0000, 5999, 5999);
INSERT INTO `oms_order_item` VALUES (28, NULL, '202204191340399551516290662706769922', 3, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB', NULL, '2', 225, 14, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB 亮黑色 5G全网通8GB+256GB', 'https://lm--gulimall.oss-cn-shanghai.aliyuncs.com/2021-12-19//3508e663-03cf-481c-8c9c-b95c5139a654_8bf441260bffa42f.jpg', 6299.0000, 1, '颜色:亮黑色;版本:5G全网通8GB+256GB', 0.0000, 0.0000, 0.0000, 6299.0000, 6299, 6299);
INSERT INTO `oms_order_item` VALUES (29, NULL, '202204191340399551516290662706769922', 3, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB', NULL, '2', 225, 13, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB 亮黑色 5G全网通8GB+128', 'https://lm--gulimall.oss-cn-shanghai.aliyuncs.com/2021-12-19//8e2b617e-2216-4302-89da-2a3dd56cb0b6_28f296629cca865e.jpg', 5999.0000, 1, '颜色:亮黑色;版本:5G全网通8GB+128', 0.0000, 0.0000, 0.0000, 5999.0000, 5999, 5999);
INSERT INTO `oms_order_item` VALUES (30, NULL, '202204191344241541516291603052961793', 3, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB', NULL, '2', 225, 14, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB 亮黑色 5G全网通8GB+256GB', 'https://lm--gulimall.oss-cn-shanghai.aliyuncs.com/2021-12-19//3508e663-03cf-481c-8c9c-b95c5139a654_8bf441260bffa42f.jpg', 6299.0000, 1, '颜色:亮黑色;版本:5G全网通8GB+256GB', 0.0000, 0.0000, 0.0000, 6299.0000, 6299, 6299);
INSERT INTO `oms_order_item` VALUES (31, NULL, '202204191344241541516291603052961793', 3, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB', NULL, '2', 225, 13, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB 亮黑色 5G全网通8GB+128', 'https://lm--gulimall.oss-cn-shanghai.aliyuncs.com/2021-12-19//8e2b617e-2216-4302-89da-2a3dd56cb0b6_28f296629cca865e.jpg', 5999.0000, 1, '颜色:亮黑色;版本:5G全网通8GB+128', 0.0000, 0.0000, 0.0000, 5999.0000, 5999, 5999);
INSERT INTO `oms_order_item` VALUES (32, NULL, '202204191353191341516293846913318913', 3, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB', NULL, '2', 225, 14, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB 亮黑色 5G全网通8GB+256GB', 'https://lm--gulimall.oss-cn-shanghai.aliyuncs.com/2021-12-19//3508e663-03cf-481c-8c9c-b95c5139a654_8bf441260bffa42f.jpg', 6299.0000, 1, '颜色:亮黑色;版本:5G全网通8GB+256GB', 0.0000, 0.0000, 0.0000, 6299.0000, 6299, 6299);
INSERT INTO `oms_order_item` VALUES (33, NULL, '202204191353191341516293846913318913', 3, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB', NULL, '2', 225, 13, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB 亮黑色 5G全网通8GB+128', 'https://lm--gulimall.oss-cn-shanghai.aliyuncs.com/2021-12-19//8e2b617e-2216-4302-89da-2a3dd56cb0b6_28f296629cca865e.jpg', 5999.0000, 2, '颜色:亮黑色;版本:5G全网通8GB+128', 0.0000, 0.0000, 0.0000, 11998.0000, 11998, 11998);
INSERT INTO `oms_order_item` VALUES (34, NULL, '202204191356557671516294755546353665', 3, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB', NULL, '2', 225, 14, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB 亮黑色 5G全网通8GB+256GB', 'https://lm--gulimall.oss-cn-shanghai.aliyuncs.com/2021-12-19//3508e663-03cf-481c-8c9c-b95c5139a654_8bf441260bffa42f.jpg', 6299.0000, 1, '颜色:亮黑色;版本:5G全网通8GB+256GB', 0.0000, 0.0000, 0.0000, 6299.0000, 6299, 6299);
INSERT INTO `oms_order_item` VALUES (35, NULL, '202204191356557671516294755546353665', 3, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB', NULL, '2', 225, 13, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB 亮黑色 5G全网通8GB+128', 'https://lm--gulimall.oss-cn-shanghai.aliyuncs.com/2021-12-19//8e2b617e-2216-4302-89da-2a3dd56cb0b6_28f296629cca865e.jpg', 5999.0000, 1, '颜色:亮黑色;版本:5G全网通8GB+128', 0.0000, 0.0000, 0.0000, 5999.0000, 5999, 5999);
INSERT INTO `oms_order_item` VALUES (36, NULL, '202204211655480571517064545764466689', 3, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB', NULL, '2', 225, 14, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB 亮黑色 5G全网通8GB+256GB', 'https://lm--gulimall.oss-cn-shanghai.aliyuncs.com/2021-12-19//3508e663-03cf-481c-8c9c-b95c5139a654_8bf441260bffa42f.jpg', 6299.0000, 1, '颜色:亮黑色;版本:5G全网通8GB+256GB', 0.0000, 0.0000, 0.0000, 6299.0000, 6299, 6299);
INSERT INTO `oms_order_item` VALUES (37, NULL, '202204211655480571517064545764466689', 3, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB', NULL, '2', 225, 13, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB 亮黑色 5G全网通8GB+128', 'https://lm--gulimall.oss-cn-shanghai.aliyuncs.com/2021-12-19//8e2b617e-2216-4302-89da-2a3dd56cb0b6_28f296629cca865e.jpg', 5999.0000, 1, '颜色:亮黑色;版本:5G全网通8GB+128', 0.0000, 0.0000, 0.0000, 5999.0000, 5999, 5999);
INSERT INTO `oms_order_item` VALUES (38, NULL, '202204211707298811517067489423482882', 3, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB', NULL, '2', 225, 14, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB 亮黑色 5G全网通8GB+256GB', 'https://lm--gulimall.oss-cn-shanghai.aliyuncs.com/2021-12-19//3508e663-03cf-481c-8c9c-b95c5139a654_8bf441260bffa42f.jpg', 6299.0000, 1, '颜色:亮黑色;版本:5G全网通8GB+256GB', 0.0000, 0.0000, 0.0000, 6299.0000, 6299, 6299);
INSERT INTO `oms_order_item` VALUES (39, NULL, '202204211707298811517067489423482882', 3, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB', NULL, '2', 225, 13, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB 亮黑色 5G全网通8GB+128', 'https://lm--gulimall.oss-cn-shanghai.aliyuncs.com/2021-12-19//8e2b617e-2216-4302-89da-2a3dd56cb0b6_28f296629cca865e.jpg', 5999.0000, 1, '颜色:亮黑色;版本:5G全网通8GB+128', 0.0000, 0.0000, 0.0000, 5999.0000, 5999, 5999);
INSERT INTO `oms_order_item` VALUES (40, NULL, '202204221319209551517372461782495233', 3, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB', NULL, '2', 225, 14, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB 亮黑色 5G全网通8GB+256GB', 'https://lm--gulimall.oss-cn-shanghai.aliyuncs.com/2021-12-19//3508e663-03cf-481c-8c9c-b95c5139a654_8bf441260bffa42f.jpg', 6299.0000, 1, '颜色:亮黑色;版本:5G全网通8GB+256GB', 0.0000, 0.0000, 0.0000, 6299.0000, 6299, 6299);
INSERT INTO `oms_order_item` VALUES (41, NULL, '202204221319209551517372461782495233', 3, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB', NULL, '2', 225, 13, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB 亮黑色 5G全网通8GB+128', 'https://lm--gulimall.oss-cn-shanghai.aliyuncs.com/2021-12-19//8e2b617e-2216-4302-89da-2a3dd56cb0b6_28f296629cca865e.jpg', 5999.0000, 1, '颜色:亮黑色;版本:5G全网通8GB+128', 0.0000, 0.0000, 0.0000, 5999.0000, 5999, 5999);
INSERT INTO `oms_order_item` VALUES (42, NULL, '202204221328175801517374712538210306', 3, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB', NULL, '2', 225, 14, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB 亮黑色 5G全网通8GB+256GB', 'https://lm--gulimall.oss-cn-shanghai.aliyuncs.com/2021-12-19//3508e663-03cf-481c-8c9c-b95c5139a654_8bf441260bffa42f.jpg', 6299.0000, 1, '颜色:亮黑色;版本:5G全网通8GB+256GB', 0.0000, 0.0000, 0.0000, 6299.0000, 6299, 6299);
INSERT INTO `oms_order_item` VALUES (43, NULL, '202204221342129601517378216354914306', 3, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB', NULL, '2', 225, 14, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB 亮黑色 5G全网通8GB+256GB', 'https://lm--gulimall.oss-cn-shanghai.aliyuncs.com/2021-12-19//3508e663-03cf-481c-8c9c-b95c5139a654_8bf441260bffa42f.jpg', 6299.0000, 2, '颜色:亮黑色;版本:5G全网通8GB+256GB', 0.0000, 0.0000, 0.0000, 12598.0000, 12598, 12598);
INSERT INTO `oms_order_item` VALUES (44, NULL, '202204221405390451517384113928019970', 3, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB', NULL, '2', 225, 16, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB 亮黑色 4G全网通8GB+128GB', 'https://lm--gulimall.oss-cn-shanghai.aliyuncs.com/2021-12-19//8e2b617e-2216-4302-89da-2a3dd56cb0b6_28f296629cca865e.jpg', 4999.0000, 2, '颜色:亮黑色;版本:4G全网通8GB+128GB', 0.0000, 0.0000, 0.0000, 9998.0000, 9998, 9998);
INSERT INTO `oms_order_item` VALUES (45, NULL, '202204221405390451517384113928019970', 3, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB', NULL, '2', 225, 14, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB 亮黑色 5G全网通8GB+256GB', 'https://lm--gulimall.oss-cn-shanghai.aliyuncs.com/2021-12-19//3508e663-03cf-481c-8c9c-b95c5139a654_8bf441260bffa42f.jpg', 6299.0000, 2, '颜色:亮黑色;版本:5G全网通8GB+256GB', 0.0000, 0.0000, 0.0000, 12598.0000, 12598, 12598);
INSERT INTO `oms_order_item` VALUES (46, NULL, '202204221407028261517384465301643266', 3, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB', NULL, '2', 225, 14, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB 亮黑色 5G全网通8GB+256GB', 'https://lm--gulimall.oss-cn-shanghai.aliyuncs.com/2021-12-19//3508e663-03cf-481c-8c9c-b95c5139a654_8bf441260bffa42f.jpg', 6299.0000, 1, '颜色:亮黑色;版本:5G全网通8GB+256GB', 0.0000, 0.0000, 0.0000, 6299.0000, 6299, 6299);
INSERT INTO `oms_order_item` VALUES (47, NULL, '202204221840552251517453387923263490', 3, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB', NULL, '2', 225, 13, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB 亮黑色 5G全网通8GB+128', 'https://lm--gulimall.oss-cn-shanghai.aliyuncs.com/2021-12-19//8e2b617e-2216-4302-89da-2a3dd56cb0b6_28f296629cca865e.jpg', 5999.0000, 1, '颜色:亮黑色;版本:5G全网通8GB+128', 0.0000, 0.0000, 0.0000, 5999.0000, 5999, 5999);
INSERT INTO `oms_order_item` VALUES (48, NULL, '202204221846154931517454731098746882', 3, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB', NULL, '2', 225, 14, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB 亮黑色 5G全网通8GB+256GB', 'https://lm--gulimall.oss-cn-shanghai.aliyuncs.com/2021-12-19//3508e663-03cf-481c-8c9c-b95c5139a654_8bf441260bffa42f.jpg', 6299.0000, 1, '颜色:亮黑色;版本:5G全网通8GB+256GB', 0.0000, 0.0000, 0.0000, 6299.0000, 6299, 6299);
INSERT INTO `oms_order_item` VALUES (49, NULL, '202204221850062771517455699081183233', 3, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB', NULL, '2', 225, 14, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB 亮黑色 5G全网通8GB+256GB', 'https://lm--gulimall.oss-cn-shanghai.aliyuncs.com/2021-12-19//3508e663-03cf-481c-8c9c-b95c5139a654_8bf441260bffa42f.jpg', 6299.0000, 1, '颜色:亮黑色;版本:5G全网通8GB+256GB', 0.0000, 0.0000, 0.0000, 6299.0000, 6299, 6299);
INSERT INTO `oms_order_item` VALUES (50, NULL, '202204221857195281517457516267589634', 3, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB', NULL, '2', 225, 14, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB 亮黑色 5G全网通8GB+256GB', 'https://lm--gulimall.oss-cn-shanghai.aliyuncs.com/2021-12-19//3508e663-03cf-481c-8c9c-b95c5139a654_8bf441260bffa42f.jpg', 6299.0000, 2, '颜色:亮黑色;版本:5G全网通8GB+256GB', 0.0000, 0.0000, 0.0000, 12598.0000, 12598, 12598);
INSERT INTO `oms_order_item` VALUES (51, NULL, '202204221927495661517465192007495682', 3, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB', NULL, '2', 225, 13, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB 亮黑色 5G全网通8GB+128', 'https://lm--gulimall.oss-cn-shanghai.aliyuncs.com/2021-12-19//8e2b617e-2216-4302-89da-2a3dd56cb0b6_28f296629cca865e.jpg', 5999.0000, 1, '颜色:亮黑色;版本:5G全网通8GB+128', 0.0000, 0.0000, 0.0000, 5999.0000, 5999, 5999);
INSERT INTO `oms_order_item` VALUES (52, NULL, '202204221950000811517470772591874049', 3, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB', NULL, '2', 225, 14, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB 亮黑色 5G全网通8GB+256GB', 'https://lm--gulimall.oss-cn-shanghai.aliyuncs.com/2021-12-19//3508e663-03cf-481c-8c9c-b95c5139a654_8bf441260bffa42f.jpg', 6299.0000, 1, '颜色:亮黑色;版本:5G全网通8GB+256GB', 0.0000, 0.0000, 0.0000, 6299.0000, 6299, 6299);
INSERT INTO `oms_order_item` VALUES (53, NULL, '202204221954508681517471992240963586', 3, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB', NULL, '2', 225, 14, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB 亮黑色 5G全网通8GB+256GB', 'https://lm--gulimall.oss-cn-shanghai.aliyuncs.com/2021-12-19//3508e663-03cf-481c-8c9c-b95c5139a654_8bf441260bffa42f.jpg', 6299.0000, 1, '颜色:亮黑色;版本:5G全网通8GB+256GB', 0.0000, 0.0000, 0.0000, 6299.0000, 6299, 6299);
INSERT INTO `oms_order_item` VALUES (54, NULL, '202204222001055861517473563922169857', 3, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB', NULL, '2', 225, 14, '华为 HUAWEI Mate 30 Pro 5G 麒麟990 OLED环幕屏双4000万徕卡电影四摄 丹霞橙 4G全网通8GB+256GB 亮黑色 5G全网通8GB+256GB', 'https://lm--gulimall.oss-cn-shanghai.aliyuncs.com/2021-12-19//3508e663-03cf-481c-8c9c-b95c5139a654_8bf441260bffa42f.jpg', 6299.0000, 1, '颜色:亮黑色;版本:5G全网通8GB+256GB', 0.0000, 0.0000, 0.0000, 6299.0000, 6299, 6299);

-- ----------------------------
-- Table structure for oms_order_operate_history
-- ----------------------------
DROP TABLE IF EXISTS `oms_order_operate_history`;
CREATE TABLE `oms_order_operate_history`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `order_id` bigint(20) NULL DEFAULT NULL COMMENT '����id',
  `operate_man` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '������[�û���ϵͳ����̨����Ա]',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '����ʱ��',
  `order_status` tinyint(4) NULL DEFAULT NULL COMMENT '����״̬��0->�����1->��������2->�ѷ�����3->����ɣ�4->�ѹرգ�5->��Ч������',
  `note` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '��ע',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '����������ʷ��¼' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of oms_order_operate_history
-- ----------------------------

-- ----------------------------
-- Table structure for oms_order_return_apply
-- ----------------------------
DROP TABLE IF EXISTS `oms_order_return_apply`;
CREATE TABLE `oms_order_return_apply`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `order_id` bigint(20) NULL DEFAULT NULL COMMENT 'order_id',
  `sku_id` bigint(20) NULL DEFAULT NULL COMMENT '�˻���Ʒid',
  `order_sn` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '�������',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '����ʱ��',
  `member_username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '��Ա�û���',
  `return_amount` decimal(18, 4) NULL DEFAULT NULL COMMENT '�˿���',
  `return_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '�˻�������',
  `return_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '�˻��˵绰',
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '����״̬[0->��������1->�˻��У�2->����ɣ�3->�Ѿܾ�]',
  `handle_time` datetime(0) NULL DEFAULT NULL COMMENT '����ʱ��',
  `sku_img` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '��ƷͼƬ',
  `sku_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '��Ʒ����',
  `sku_brand` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '��ƷƷ��',
  `sku_attrs_vals` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '��Ʒ��������(JSON)',
  `sku_count` int(11) NULL DEFAULT NULL COMMENT '�˻�����',
  `sku_price` decimal(18, 4) NULL DEFAULT NULL COMMENT '��Ʒ����',
  `sku_real_price` decimal(18, 4) NULL DEFAULT NULL COMMENT '��Ʒʵ��֧������',
  `reason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ԭ��',
  `description��` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '����',
  `desc_pics` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ƾ֤ͼƬ���Զ��Ÿ���',
  `handle_note` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '������ע',
  `handle_man` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '������Ա',
  `receive_man` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '�ջ���',
  `receive_time` datetime(0) NULL DEFAULT NULL COMMENT '�ջ�ʱ��',
  `receive_note` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '�ջ���ע',
  `receive_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '�ջ��绰',
  `company_address` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '��˾�ջ���ַ',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '�����˻�����' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of oms_order_return_apply
-- ----------------------------

-- ----------------------------
-- Table structure for oms_order_return_reason
-- ----------------------------
DROP TABLE IF EXISTS `oms_order_return_reason`;
CREATE TABLE `oms_order_return_reason`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '�˻�ԭ����',
  `sort` int(11) NULL DEFAULT NULL COMMENT '����',
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '����״̬',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT 'create_time',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '�˻�ԭ��' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of oms_order_return_reason
-- ----------------------------

-- ----------------------------
-- Table structure for oms_order_setting
-- ----------------------------
DROP TABLE IF EXISTS `oms_order_setting`;
CREATE TABLE `oms_order_setting`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `flash_order_overtime` int(11) NULL DEFAULT NULL COMMENT '��ɱ������ʱ�ر�ʱ��(��)',
  `normal_order_overtime` int(11) NULL DEFAULT NULL COMMENT '����������ʱʱ��(��)',
  `confirm_overtime` int(11) NULL DEFAULT NULL COMMENT '�������Զ�ȷ���ջ�ʱ�䣨�죩',
  `finish_overtime` int(11) NULL DEFAULT NULL COMMENT '�Զ���ɽ���ʱ�䣬���������˻����죩',
  `comment_overtime` int(11) NULL DEFAULT NULL COMMENT '������ɺ��Զ�����ʱ�䣨�죩',
  `member_level` tinyint(2) NULL DEFAULT NULL COMMENT '��Ա�ȼ���0-���޻�Ա�ȼ���ȫ��ͨ�ã�����-��Ӧ��������Ա�ȼ���',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '����������Ϣ' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of oms_order_setting
-- ----------------------------

-- ----------------------------
-- Table structure for oms_payment_info
-- ----------------------------
DROP TABLE IF EXISTS `oms_payment_info`;
CREATE TABLE `oms_payment_info`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `order_sn` char(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '�����ţ�����ҵ��ţ�',
  `order_id` bigint(20) NULL DEFAULT NULL COMMENT '����id',
  `alipay_trade_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '֧����������ˮ��',
  `total_amount` decimal(18, 4) NULL DEFAULT NULL COMMENT '֧���ܽ��',
  `subject` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '��������',
  `payment_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '֧��״̬',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '����ʱ��',
  `confirm_time` datetime(0) NULL DEFAULT NULL COMMENT 'ȷ��ʱ��',
  `callback_content` varchar(4000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '�ص�����',
  `callback_time` datetime(0) NULL DEFAULT NULL COMMENT '�ص�ʱ��',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `订单号`(`order_sn`) USING BTREE,
  UNIQUE INDEX `支付宝交易号`(`alipay_trade_no`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '֧����Ϣ��' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of oms_payment_info
-- ----------------------------
INSERT INTO `oms_payment_info` VALUES (1, '202204221857195281517457516267589634', NULL, '2022042222001421060501514581', NULL, NULL, 'TRADE_SUCCESS', '2022-04-22 10:57:46', NULL, NULL, '2022-04-22 10:57:41');
INSERT INTO `oms_payment_info` VALUES (2, '202204221950000811517470772591874049', NULL, '2022042222001421060501514733', NULL, NULL, 'TRADE_SUCCESS', '2022-04-22 11:51:31', NULL, NULL, '2022-04-22 11:51:28');

-- ----------------------------
-- Table structure for oms_refund_info
-- ----------------------------
DROP TABLE IF EXISTS `oms_refund_info`;
CREATE TABLE `oms_refund_info`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `order_return_id` bigint(20) NULL DEFAULT NULL COMMENT '�˿�Ķ���',
  `refund` decimal(18, 4) NULL DEFAULT NULL COMMENT '�˿���',
  `refund_sn` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '�˿����ˮ��',
  `refund_status` tinyint(1) NULL DEFAULT NULL COMMENT '�˿�״̬',
  `refund_channel` tinyint(4) NULL DEFAULT NULL COMMENT '�˿�����[1-֧������2-΢�ţ�3-������4-���]',
  `refund_content` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '�˿���Ϣ' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of oms_refund_info
-- ----------------------------

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
  `log_created` datetime(0) NOT NULL,
  `log_modified` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `ext` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `ux_undo_log`(`xid`, `branch_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of undo_log
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
