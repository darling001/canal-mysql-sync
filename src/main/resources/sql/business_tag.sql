CREATE TABLE product.tag
(
    id int(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,
    type varchar(45),
    attr varchar(45)
);
INSERT INTO product.tag (id, type, attr) VALUES (1, 'model', '{name:"名称",child:"子集"}');
INSERT INTO product.tag (id, type, attr) VALUES (2, 'img_link', '{src:"图片地址", url:"链接地址"}');
INSERT INTO product.tag (id, type, attr) VALUES (3, 'str_link', '{name:"链接名称",url:"链接地址"}');