CREATE TABLE product.tag_info
(
    tag_info_id int(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,
    tag_id int(11),
    pid int(11) COMMENT '父ID',
    attr_info json COMMENT '属性信息',
    style_type varchar(45),
    CONSTRAINT tag_id FOREIGN KEY (tag_id) REFERENCES tag (id)
);
CREATE INDEX tag_id_idx ON product.tag_info (tag_id);
INSERT INTO product.tag_info (tag_info_id, tag_id, pid, attr_info, style_type) VALUES (1, 1, 0, '{"name": "卫浴", "child": [2, 3, 4]}', 'model_css');
INSERT INTO product.tag_info (tag_info_id, tag_id, pid, attr_info, style_type) VALUES (2, 2, 1, '{"src": "1.jpg", "url": "http://www.163.com"}', 'img_link_css');
INSERT INTO product.tag_info (tag_info_id, tag_id, pid, attr_info, style_type) VALUES (3, 2, 1, '{"src": "2.jpg", "url": "http://www.263.com"}', 'img_link_css');
INSERT INTO product.tag_info (tag_info_id, tag_id, pid, attr_info, style_type) VALUES (4, 1, 1, '{"name": "广告位", "child": [6, 7]}', 'model2_css');
INSERT INTO product.tag_info (tag_info_id, tag_id, pid, attr_info, style_type) VALUES (6, 2, 4, '{"src": "ad.jpg", "url": "http://www.ad.com"}', 'ad_img_link_css');
INSERT INTO product.tag_info (tag_info_id, tag_id, pid, attr_info, style_type) VALUES (7, 3, 4, '{"url": "http://www.ad1.com", "name": "广告1"}', 'ad_str_link_css');