DROP SCHEMA IF EXISTS `scala-project`;

CREATE SCHEMA `scala-project`;

USE `scala-project`;

SET FOREIGN_KEY_CHECKS = 0;

--
-- Base de données :  `scala-project`
--

-- --------------------------------------------------------

--
-- Structure de la table `users`
--

CREATE TABLE `users` (
  `email` VARCHAR(100) NOT NULL,
  `password` text NOT NULL,
  `created_at` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Index pour la table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`email`(100));

--
-- Structure de la table `tweets`
--

CREATE TABLE `tweets` (
  `id` BIGINT AUTO_INCREMENT,
  `author_screen_name` text NOT NULL,
  `tweet_id` text NOT NULL,
  `full_text` text NOT NULL,
  `created_at` date NOT NULL,
  `analyzed_at` date NOT NULL,
  `avg_sentiment` text NOT NULL,
  `user_id` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`user_id`) REFERENCES users(`email`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Structure de la table `tweet_responses`
--


CREATE TABLE `tweet_responses` (
  `id` BIGINT AUTO_INCREMENT,
  `author_screen_name` text NOT NULL,
  `tweet_id` text NOT NULL,
  `full_text` text NOT NULL,
  `created_at` date NOT NULL,
  `analyzed_at` date NOT NULL,
  `sentiment` INT NOT NULL,
  `user_id` VARCHAR(100) NOT NULL,
  `fk_tweet` BIGINT,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`user_id`) REFERENCES users(`email`) ON DELETE CASCADE,
  FOREIGN KEY (`fk_tweet`) REFERENCES tweets(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

SET FOREIGN_KEY_CHECKS = 1;