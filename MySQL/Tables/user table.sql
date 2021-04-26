use d2090704;
CREATE TABLE IF NOT EXISTS `d2090704`.`users` (
`user_id` int(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
`username` varchar(70) NOT NULL,
`password` varchar(255),
`email` varchar(50) NOT NULL,
`salt` varchar(255),
`created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
`updated_at` timestamp NOT NULL,
`password_reset_token` varchar(255) unique,
`token_expiration` dateTime
);
