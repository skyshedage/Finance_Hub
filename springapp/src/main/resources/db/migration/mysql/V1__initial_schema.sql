-- Fresh empty MySQL 8 schema. No automatic baseline or destructive schema reset.
CREATE TABLE `user` (
  `registration_date` date NOT NULL,
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) DEFAULT NULL,
  `mobile_number` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `user_role` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `UK_ob8kqyqqgmefl0aco34akdtpe` (`email`),
  UNIQUE KEY `UK_lt2b8ocin3hdnm9q6fm4o8xnr` (`mobile_number`),
  UNIQUE KEY `UK_sb8bbouer5wak8vyiiy4pf2bx` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `savings_plan` (
  `goal_amount` double NOT NULL,
  `time_frame` int NOT NULL,
  `savings_plan_id` bigint NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `risk_level` varchar(255) DEFAULT NULL,
  `status` varchar(255) NOT NULL,
  PRIMARY KEY (`savings_plan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `enquiry` (
  `enquiry_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  `message` varchar(255) DEFAULT NULL,
  `reply_from_manager` varchar(255) DEFAULT NULL,
  `status` varchar(255) NOT NULL,
  PRIMARY KEY (`enquiry_id`),
  KEY `FKajncquib9guat5jtaxnxxcayt` (`user_id`),
  CONSTRAINT `FKajncquib9guat5jtaxnxxcayt` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `feedback` (
  `date` date DEFAULT NULL,
  `feedback_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  `feedback_text` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`feedback_id`),
  KEY `FK7k33yw505d347mw3avr93akao` (`user_id`),
  CONSTRAINT `FK7k33yw505d347mw3avr93akao` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `plan_application` (
  `applied_amount` double NOT NULL,
  `date` date DEFAULT NULL,
  `plan_application_id` bigint NOT NULL AUTO_INCREMENT,
  `savings_plan_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `image` varchar(255) DEFAULT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `proof_data` longblob,
  PRIMARY KEY (`plan_application_id`),
  KEY `FKuw3y99wqju1vxfx0h8foywuu` (`savings_plan_id`),
  KEY `FKpw1xqfeui0poveicdrr631jt8` (`user_id`),
  CONSTRAINT `FKpw1xqfeui0poveicdrr631jt8` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
  CONSTRAINT `FKuw3y99wqju1vxfx0h8foywuu` FOREIGN KEY (`savings_plan_id`) REFERENCES `savings_plan` (`savings_plan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
