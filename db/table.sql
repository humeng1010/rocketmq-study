SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for goods
-- ----------------------------
DROP TABLE IF EXISTS `goods`;
CREATE TABLE `goods`
(
    `id`          int(11)                                                       NOT NULL AUTO_INCREMENT,
    `goods_name`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    `price`       decimal(10, 2)                                                NULL DEFAULT NULL,
    `stocks`      int(255)                                                      NULL DEFAULT NULL,
    `status`      int(255)                                                      NULL DEFAULT NULL,
    `pic`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    `create_time` datetime(0)                                                   NULL DEFAULT NULL,
    `update_time` datetime(0)                                                   NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 4
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of goods
-- ----------------------------
INSERT INTO `goods`
VALUES (1, '小米12s', 4999.00, 1000, 2, 'xxxxxx', '2023-02-23 11:35:56', '2023-02-23 16:53:34');
INSERT INTO `goods`
VALUES (2, '华为mate50', 6999.00, 10, 2, 'xxxx', '2023-02-23 11:35:56', '2023-02-23 11:35:56');
INSERT INTO `goods`
VALUES (3, '锤子pro2', 1999.00, 100, 1, NULL, '2023-02-23 11:35:56', '2023-02-23 11:35:56');

-- ----------------------------
-- Table structure for order_records
-- ----------------------------
DROP TABLE IF EXISTS `order_records`;
CREATE TABLE `order_records`
(
    `id`          int(11)                                                       NOT NULL AUTO_INCREMENT,
    `user_id`     int(11)                                                       NULL DEFAULT NULL,
    `order_sn`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    `goods_id`    int(11)                                                       NULL DEFAULT NULL,
    `create_time` datetime(0)                                                   NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;