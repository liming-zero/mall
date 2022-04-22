/*
 Navicat MySQL Data Transfer

 Source Server         : 192.168.247.130
 Source Server Type    : MySQL
 Source Server Version : 50736
 Source Host           : 192.168.247.130:3306
 Source Schema         : mall_sms

 Target Server Type    : MySQL
 Target Server Version : 50736
 File Encoding         : 65001

 Date: 23/04/2022 02:52:43
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sms_coupon
-- ----------------------------
DROP TABLE IF EXISTS `sms_coupon`;
CREATE TABLE `sms_coupon`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `coupon_type` tinyint(1) NULL DEFAULT NULL COMMENT '�Żݾ�����[0->ȫ����ȯ��1->��Ա��ȯ��2->������ȯ��3->ע����ȯ]',
  `coupon_img` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '�Ż�ȯͼƬ',
  `coupon_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '�Żݾ�����',
  `num` int(11) NULL DEFAULT NULL COMMENT '����',
  `amount` decimal(18, 4) NULL DEFAULT NULL COMMENT '���',
  `per_limit` int(11) NULL DEFAULT NULL COMMENT 'ÿ����������',
  `min_point` decimal(18, 4) NULL DEFAULT NULL COMMENT 'ʹ���ż�',
  `start_time` datetime(0) NULL DEFAULT NULL COMMENT '��ʼʱ��',
  `end_time` datetime(0) NULL DEFAULT NULL COMMENT '����ʱ��',
  `use_type` tinyint(1) NULL DEFAULT NULL COMMENT 'ʹ������[0->ȫ��ͨ�ã�1->ָ�����ࣻ2->ָ����Ʒ]',
  `note` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '��ע',
  `publish_count` int(11) NULL DEFAULT NULL COMMENT '��������',
  `use_count` int(11) NULL DEFAULT NULL COMMENT '��ʹ������',
  `receive_count` int(11) NULL DEFAULT NULL COMMENT '��ȡ����',
  `enable_start_time` datetime(0) NULL DEFAULT NULL COMMENT '������ȡ�Ŀ�ʼ����',
  `enable_end_time` datetime(0) NULL DEFAULT NULL COMMENT '������ȡ�Ľ�������',
  `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '�Ż���',
  `member_level` tinyint(1) NULL DEFAULT NULL COMMENT '������ȡ�Ļ�Ա�ȼ�[0->���޵ȼ�������-��Ӧ�ȼ�]',
  `publish` tinyint(1) NULL DEFAULT NULL COMMENT '����״̬[0-δ������1-�ѷ���]',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '�Ż�ȯ��Ϣ' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sms_coupon
-- ----------------------------

-- ----------------------------
-- Table structure for sms_coupon_history
-- ----------------------------
DROP TABLE IF EXISTS `sms_coupon_history`;
CREATE TABLE `sms_coupon_history`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `coupon_id` bigint(20) NULL DEFAULT NULL COMMENT '�Ż�ȯid',
  `member_id` bigint(20) NULL DEFAULT NULL COMMENT '��Աid',
  `member_nick_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '��Ա����',
  `get_type` tinyint(1) NULL DEFAULT NULL COMMENT '��ȡ��ʽ[0->��̨���ͣ�1->������ȡ]',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '����ʱ��',
  `use_type` tinyint(1) NULL DEFAULT NULL COMMENT 'ʹ��״̬[0->δʹ�ã�1->��ʹ�ã�2->�ѹ���]',
  `use_time` datetime(0) NULL DEFAULT NULL COMMENT 'ʹ��ʱ��',
  `order_id` bigint(20) NULL DEFAULT NULL COMMENT '����id',
  `order_sn` bigint(20) NULL DEFAULT NULL COMMENT '������',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '�Ż�ȯ��ȡ��ʷ��¼' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sms_coupon_history
-- ----------------------------

-- ----------------------------
-- Table structure for sms_coupon_spu_category_relation
-- ----------------------------
DROP TABLE IF EXISTS `sms_coupon_spu_category_relation`;
CREATE TABLE `sms_coupon_spu_category_relation`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `coupon_id` bigint(20) NULL DEFAULT NULL COMMENT '�Ż�ȯid',
  `category_id` bigint(20) NULL DEFAULT NULL COMMENT '��Ʒ����id',
  `category_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '��Ʒ��������',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '�Ż�ȯ�������' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sms_coupon_spu_category_relation
-- ----------------------------

-- ----------------------------
-- Table structure for sms_coupon_spu_relation
-- ----------------------------
DROP TABLE IF EXISTS `sms_coupon_spu_relation`;
CREATE TABLE `sms_coupon_spu_relation`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `coupon_id` bigint(20) NULL DEFAULT NULL COMMENT '�Ż�ȯid',
  `spu_id` bigint(20) NULL DEFAULT NULL COMMENT 'spu_id',
  `spu_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'spu_name',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '�Ż�ȯ���Ʒ����' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sms_coupon_spu_relation
-- ----------------------------

-- ----------------------------
-- Table structure for sms_home_adv
-- ----------------------------
DROP TABLE IF EXISTS `sms_home_adv`;
CREATE TABLE `sms_home_adv`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '����',
  `pic` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ͼƬ��ַ',
  `start_time` datetime(0) NULL DEFAULT NULL COMMENT '��ʼʱ��',
  `end_time` datetime(0) NULL DEFAULT NULL COMMENT '����ʱ��',
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '״̬',
  `click_count` int(11) NULL DEFAULT NULL COMMENT '�����',
  `url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '����������ӵ�ַ',
  `note` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '��ע',
  `sort` int(11) NULL DEFAULT NULL COMMENT '����',
  `publisher_id` bigint(20) NULL DEFAULT NULL COMMENT '������',
  `auth_id` bigint(20) NULL DEFAULT NULL COMMENT '�����',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '��ҳ�ֲ����' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sms_home_adv
-- ----------------------------

-- ----------------------------
-- Table structure for sms_home_subject
-- ----------------------------
DROP TABLE IF EXISTS `sms_home_subject`;
CREATE TABLE `sms_home_subject`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ר������',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ר�����',
  `sub_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ר�⸱����',
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '��ʾ״̬',
  `url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '��������',
  `sort` int(11) NULL DEFAULT NULL COMMENT '����',
  `img` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ר��ͼƬ��ַ',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '��ҳר�����jd��ҳ����ܶ�ר�⣬ÿ��ר�������µ�ҳ�棬չʾר����Ʒ��Ϣ��' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sms_home_subject
-- ----------------------------

-- ----------------------------
-- Table structure for sms_home_subject_spu
-- ----------------------------
DROP TABLE IF EXISTS `sms_home_subject_spu`;
CREATE TABLE `sms_home_subject_spu`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ר������',
  `subject_id` bigint(20) NULL DEFAULT NULL COMMENT 'ר��id',
  `spu_id` bigint(20) NULL DEFAULT NULL COMMENT 'spu_id',
  `sort` int(11) NULL DEFAULT NULL COMMENT '����',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'ר����Ʒ' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sms_home_subject_spu
-- ----------------------------

-- ----------------------------
-- Table structure for sms_member_price
-- ----------------------------
DROP TABLE IF EXISTS `sms_member_price`;
CREATE TABLE `sms_member_price`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `sku_id` bigint(20) NULL DEFAULT NULL COMMENT 'sku_id',
  `member_level_id` bigint(20) NULL DEFAULT NULL COMMENT '��Ա�ȼ�id',
  `member_level_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '��Ա�ȼ���',
  `member_price` decimal(18, 4) NULL DEFAULT NULL COMMENT '��Ա��Ӧ�۸�',
  `add_other` tinyint(1) NULL DEFAULT NULL COMMENT '�ɷ���������Ż�[0-���ɵ����Żݣ�1-�ɵ���]',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '��Ʒ��Ա�۸�' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sms_member_price
-- ----------------------------

-- ----------------------------
-- Table structure for sms_seckill_promotion
-- ----------------------------
DROP TABLE IF EXISTS `sms_seckill_promotion`;
CREATE TABLE `sms_seckill_promotion`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '�����',
  `start_time` datetime(0) NULL DEFAULT NULL COMMENT '��ʼ����',
  `end_time` datetime(0) NULL DEFAULT NULL COMMENT '��������',
  `status` tinyint(4) NULL DEFAULT NULL COMMENT '������״̬',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '����ʱ��',
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '������',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '��ɱ�' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sms_seckill_promotion
-- ----------------------------

-- ----------------------------
-- Table structure for sms_seckill_session
-- ----------------------------
DROP TABLE IF EXISTS `sms_seckill_session`;
CREATE TABLE `sms_seckill_session`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '��������',
  `start_time` datetime(0) NULL DEFAULT NULL COMMENT 'ÿ�տ�ʼʱ��',
  `end_time` datetime(0) NULL DEFAULT NULL COMMENT 'ÿ�ս���ʱ��',
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '����״̬',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '����ʱ��',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '��ɱ�����' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sms_seckill_session
-- ----------------------------

-- ----------------------------
-- Table structure for sms_seckill_sku_notice
-- ----------------------------
DROP TABLE IF EXISTS `sms_seckill_sku_notice`;
CREATE TABLE `sms_seckill_sku_notice`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `member_id` bigint(20) NULL DEFAULT NULL COMMENT 'member_id',
  `sku_id` bigint(20) NULL DEFAULT NULL COMMENT 'sku_id',
  `session_id` bigint(20) NULL DEFAULT NULL COMMENT '�����id',
  `subcribe_time` datetime(0) NULL DEFAULT NULL COMMENT '����ʱ��',
  `send_time` datetime(0) NULL DEFAULT NULL COMMENT '����ʱ��',
  `notice_type` tinyint(1) NULL DEFAULT NULL COMMENT '֪ͨ��ʽ[0-���ţ�1-�ʼ�]',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '��ɱ��Ʒ֪ͨ����' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sms_seckill_sku_notice
-- ----------------------------

-- ----------------------------
-- Table structure for sms_seckill_sku_relation
-- ----------------------------
DROP TABLE IF EXISTS `sms_seckill_sku_relation`;
CREATE TABLE `sms_seckill_sku_relation`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `promotion_id` bigint(20) NULL DEFAULT NULL COMMENT '�id',
  `promotion_session_id` bigint(20) NULL DEFAULT NULL COMMENT '�����id',
  `sku_id` bigint(20) NULL DEFAULT NULL COMMENT '��Ʒid',
  `seckill_price` decimal(10, 0) NULL DEFAULT NULL COMMENT '��ɱ�۸�',
  `seckill_count` decimal(10, 0) NULL DEFAULT NULL COMMENT '��ɱ����',
  `seckill_limit` decimal(10, 0) NULL DEFAULT NULL COMMENT 'ÿ���޹�����',
  `seckill_sort` int(11) NULL DEFAULT NULL COMMENT '����',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '��ɱ���Ʒ����' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sms_seckill_sku_relation
-- ----------------------------

-- ----------------------------
-- Table structure for sms_sku_full_reduction
-- ----------------------------
DROP TABLE IF EXISTS `sms_sku_full_reduction`;
CREATE TABLE `sms_sku_full_reduction`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `sku_id` bigint(20) NULL DEFAULT NULL COMMENT 'spu_id',
  `full_price` decimal(18, 4) NULL DEFAULT NULL COMMENT '������',
  `reduce_price` decimal(18, 4) NULL DEFAULT NULL COMMENT '������',
  `add_other` tinyint(1) NULL DEFAULT NULL COMMENT '�Ƿ���������Ż�',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '��Ʒ������Ϣ' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sms_sku_full_reduction
-- ----------------------------

-- ----------------------------
-- Table structure for sms_sku_ladder
-- ----------------------------
DROP TABLE IF EXISTS `sms_sku_ladder`;
CREATE TABLE `sms_sku_ladder`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `sku_id` bigint(20) NULL DEFAULT NULL COMMENT 'spu_id',
  `full_count` int(11) NULL DEFAULT NULL COMMENT '������',
  `discount` decimal(4, 2) NULL DEFAULT NULL COMMENT '����',
  `price` decimal(18, 4) NULL DEFAULT NULL COMMENT '�ۺ��',
  `add_other` tinyint(1) NULL DEFAULT NULL COMMENT '�Ƿ���������Ż�[0-���ɵ��ӣ�1-�ɵ���]',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '��Ʒ���ݼ۸�' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sms_sku_ladder
-- ----------------------------

-- ----------------------------
-- Table structure for sms_spu_bounds
-- ----------------------------
DROP TABLE IF EXISTS `sms_spu_bounds`;
CREATE TABLE `sms_spu_bounds`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `spu_id` bigint(20) NULL DEFAULT NULL,
  `grow_bounds` decimal(18, 4) NULL DEFAULT NULL COMMENT '�ɳ�����',
  `buy_bounds` decimal(18, 4) NULL DEFAULT NULL COMMENT '�������',
  `work` tinyint(1) NULL DEFAULT NULL COMMENT '�Ż���Ч���[1111���ĸ�״̬λ�����ҵ���;0 - ���Żݣ��ɳ������Ƿ�����;1 - ���Żݣ���������Ƿ�����;2 - ���Żݣ��ɳ������Ƿ�����;3 - ���Żݣ���������Ƿ����͡�״̬λ0�������ͣ�1�����͡�]',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '��Ʒspu��������' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sms_spu_bounds
-- ----------------------------
INSERT INTO `sms_spu_bounds` VALUES (1, 1, 0.0000, 0.0000, NULL);
INSERT INTO `sms_spu_bounds` VALUES (2, 2, 0.0000, 0.0000, NULL);
INSERT INTO `sms_spu_bounds` VALUES (3, 3, 500.0000, 500.0000, NULL);
INSERT INTO `sms_spu_bounds` VALUES (4, 7, 500.0000, 500.0000, NULL);
INSERT INTO `sms_spu_bounds` VALUES (5, 8, 500.0000, 500.0000, NULL);

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
  `log_modified` datetime(0) NOT NULL,
  `ext` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `ux_undo_log`(`xid`, `branch_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of undo_log
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
