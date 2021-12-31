-- phpMyAdmin SQL Dump
-- version 4.9.0.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Dec 31, 2021 at 09:57 AM
-- Server version: 10.4.6-MariaDB
-- PHP Version: 7.3.9

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `simplersps`
--

-- --------------------------------------------------------

--
-- Table structure for table `equipment`
--

CREATE TABLE `equipment` (
  `itemid` int(11) NOT NULL,
  `equipid` int(11) NOT NULL,
  `equipslot` int(11) NOT NULL,
  `covering` bit(2) NOT NULL DEFAULT b'0',
  `equipsound` int(11) NOT NULL DEFAULT 2238,
  `unequipsound` int(11) NOT NULL DEFAULT 2244,
  `blocksound` int(11) NOT NULL DEFAULT 511
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `handhelds`
--

CREATE TABLE `handhelds` (
  `itemid` int(11) NOT NULL,
  `style` bit(2) NOT NULL,
  `istwohanded` tinyint(1) NOT NULL DEFAULT 0,
  `hasspec` tinyint(1) NOT NULL DEFAULT 0,
  `idleanim` int(11) NOT NULL DEFAULT 808,
  `walkanim` int(11) NOT NULL DEFAULT 819,
  `runanim` int(11) NOT NULL DEFAULT 824,
  `attackanim` int(11) NOT NULL DEFAULT 422,
  `blockanim` int(11) NOT NULL DEFAULT 424,
  `hitsound` int(11) NOT NULL,
  `cooldown` int(11) NOT NULL DEFAULT 4,
  `styletab` int(11) NOT NULL DEFAULT 82
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `itemonitem`
--

CREATE TABLE `itemonitem` (
  `usedid` int(11) NOT NULL,
  `withid` int(11) NOT NULL,
  `usedresult` int(11) NOT NULL,
  `withresult` int(11) NOT NULL,
  `skill` int(11) NOT NULL DEFAULT -1
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `items`
--

CREATE TABLE `items` (
  `id` int(11) NOT NULL,
  `name` tinytext NOT NULL DEFAULT 'Invalid name',
  `examine` tinytext NOT NULL,
  `canstack` tinyint(1) NOT NULL DEFAULT 0,
  `isnote` tinyint(1) NOT NULL DEFAULT 0,
  `cannote` tinyint(1) NOT NULL DEFAULT 0,
  `canequip` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `npc_definitions`
--

CREATE TABLE `npc_definitions` (
  `npcid` int(11) NOT NULL,
  `name` tinytext NOT NULL,
  `examine` tinytext NOT NULL,
  `skills` varchar(48) NOT NULL DEFAULT '010101010101010101010101010101010101010101010101',
  `bonuses` varchar(24) NOT NULL DEFAULT '000000000000000000000000',
  `canattack` tinyint(1) NOT NULL DEFAULT 0,
  `attackanim` int(11) NOT NULL DEFAULT 422,
  `blockanim` int(11) NOT NULL DEFAULT 424,
  `respawn` int(11) NOT NULL DEFAULT 16
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `npc_spawns`
--

CREATE TABLE `npc_spawns` (
  `worldid` int(11) NOT NULL,
  `npcid` int(11) NOT NULL,
  `spawnx` int(11) NOT NULL,
  `spawny` int(11) NOT NULL,
  `spawnh` int(11) NOT NULL DEFAULT 0,
  `facedir` int(11) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `players`
--

CREATE TABLE `players` (
  `uuid` int(11) NOT NULL,
  `username` tinytext NOT NULL,
  `passhash` varchar(64) NOT NULL,
  `lastconnectip` tinytext NOT NULL,
  `lastconnecttime` bigint(20) NOT NULL,
  `rights` int(11) NOT NULL DEFAULT 0,
  `stat_xp` varchar(216) NOT NULL DEFAULT '0100000000100000000100000000A000049C010000000010000000010000000010000000010000000010000000010000000010000000010000000010000000010000000010000000010000000010000000010000000010000000010000000010000000010000000010000000',
  `absx` int(11) NOT NULL DEFAULT 3190,
  `absy` int(11) NOT NULL DEFAULT 3421,
  `rundata` varchar(3) NOT NULL DEFAULT '7FF'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `regions`
--

CREATE TABLE `regions` (
  `id` int(11) NOT NULL,
  `boundx1` int(11) NOT NULL,
  `boundy1` int(11) NOT NULL,
  `boundx2` int(11) NOT NULL,
  `boundy2` int(11) NOT NULL,
  `description` tinytext NOT NULL,
  `musicid` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `zones`
--

CREATE TABLE `zones` (
  `id` int(11) NOT NULL,
  `boundx1` int(11) NOT NULL,
  `boundy1` int(11) NOT NULL,
  `boundx2` int(11) NOT NULL,
  `boundy2` int(11) NOT NULL,
  `ispvp` tinyint(1) NOT NULL,
  `ismulti` tinyint(1) NOT NULL,
  `description` tinytext NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `equipment`
--
ALTER TABLE `equipment`
  ADD PRIMARY KEY (`itemid`);

--
-- Indexes for table `handhelds`
--
ALTER TABLE `handhelds`
  ADD PRIMARY KEY (`itemid`);

--
-- Indexes for table `itemonitem`
--
ALTER TABLE `itemonitem`
  ADD PRIMARY KEY (`usedid`,`withid`);

--
-- Indexes for table `items`
--
ALTER TABLE `items`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `npc_definitions`
--
ALTER TABLE `npc_definitions`
  ADD UNIQUE KEY `npcid` (`npcid`);

--
-- Indexes for table `npc_spawns`
--
ALTER TABLE `npc_spawns`
  ADD PRIMARY KEY (`worldid`),
  ADD KEY `npcid` (`npcid`);

--
-- Indexes for table `players`
--
ALTER TABLE `players`
  ADD PRIMARY KEY (`uuid`);

--
-- Indexes for table `regions`
--
ALTER TABLE `regions`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `zones`
--
ALTER TABLE `zones`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `npc_spawns`
--
ALTER TABLE `npc_spawns`
  MODIFY `worldid` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `players`
--
ALTER TABLE `players`
  MODIFY `uuid` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `regions`
--
ALTER TABLE `regions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `zones`
--
ALTER TABLE `zones`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
