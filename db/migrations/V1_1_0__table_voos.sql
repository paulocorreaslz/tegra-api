CREATE TABLE IF NOT EXISTS `flights` (
    `id` int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `voo` varchar(10),
    `origem` varchar(10),
    `destino` varchar(10),
    `data_saida` varchar(10),
    `saida` varchar(10),
    `chegada` varchar(10),
    `valor` varchar(10) 
)ENGINE=InnoDB DEFAULT CHARSET=UTF8;

INSERT INTO flights (voo) values ('TESTE');