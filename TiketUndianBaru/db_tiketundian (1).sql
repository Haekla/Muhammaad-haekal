-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Nov 27, 2026 at 07:35 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `db_tiketundian`
--

-- --------------------------------------------------------

--
-- Table structure for table `admin`
--

CREATE TABLE `admin` (
  `id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(64) NOT NULL,
  `role` varchar(10) NOT NULL DEFAULT 'ADMIN'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `admin`
--

INSERT INTO `admin` (`id`, `username`, `password`, `role`) VALUES
(1, 'admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'ADMIN'),
(2, 'haekal', '789deccedd35f8e5f22bb13bb776bd81598b579fe4454f85c1a1a28c15a7e8d3', 'ADMIN'),
(3, 'ahmad', '306098fa01257f8e4809cbdfca258d8c22c7fb12937cc2616ef06aa20fd8008e', 'ADMIN'),
(4, 'kasir', 'f02b7c1e519e4fa436147f7e1399974f9510aa9c8e0cb8be29151eb540f9d214', 'USER');

-- --------------------------------------------------------

--
-- Table structure for table `pemenang`
--

CREATE TABLE `pemenang` (
  `id` int(11) NOT NULL,
  `dibuat_pada` datetime(6) NOT NULL,
  `undian_id` int(11) NOT NULL,
  `tiket_id` int(11) NOT NULL,
  `hadiah` varchar(150) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `pemenang`
--

INSERT INTO `pemenang` (`id`, `dibuat_pada`, `undian_id`, `tiket_id`, `hadiah`) VALUES
(1, '2026-11-27 13:28:24.000000', 1, 2, 'Sepeda Motor');

-- --------------------------------------------------------

--
-- Table structure for table `peserta`
--

CREATE TABLE `peserta` (
  `id` int(11) NOT NULL,
  `dibuat_pada` datetime(6) NOT NULL,
  `nama` varchar(100) NOT NULL,
  `no_hp` varchar(20) NOT NULL,
  `alamat` varchar(150) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `peserta`
--

INSERT INTO `peserta` (`id`, `dibuat_pada`, `nama`, `no_hp`, `alamat`) VALUES
(1, '2026-11-27 13:02:04.018562', 'Andi Pratama', '081234567890', 'Depok'),
(2, '2026-11-27 13:02:04.018562', 'Siti Rahma', '081298765432', 'Jakarta Timur'),
(3, '2026-11-27 13:02:04.018562', 'Budi Santoso', '085611223344', 'Bekasi');

-- --------------------------------------------------------

--
-- Table structure for table `tiket`
--

CREATE TABLE `tiket` (
  `id` int(11) NOT NULL,
  `dibuat_pada` datetime(6) NOT NULL,
  `nomor` varchar(30) NOT NULL,
  `undian_id` int(11) NOT NULL,
  `peserta_id` int(11) NOT NULL,
  `harga` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tiket`
--

INSERT INTO `tiket` (`id`, `dibuat_pada`, `nomor`, `undian_id`, `peserta_id`, `harga`) VALUES
(1, '2026-11-27 13:02:04.035794', 'U1-0001', 1, 1, 25000),
(2, '2026-11-27 13:02:04.035794', 'U1-0002', 1, 1, 25000),
(3, '2026-11-27 13:02:04.035794', 'U1-0003', 1, 2, 25000),
(4, '2026-11-27 13:02:04.035794', 'U1-0004', 1, 3, 25000),
(5, '2026-11-27 13:02:04.035794', 'U2-0001', 2, 2, 10000);

-- --------------------------------------------------------

--
-- Table structure for table `undian`
--

CREATE TABLE `undian` (
  `id` int(11) NOT NULL,
  `dibuat_pada` datetime(6) NOT NULL,
  `nama` varchar(100) NOT NULL,
  `harga_tiket` bigint(20) NOT NULL,
  `hadiah` varchar(150) NOT NULL,
  `tanggal_undi` date NOT NULL,
  `status` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `undian`
--

INSERT INTO `undian` (`id`, `dibuat_pada`, `nama`, `harga_tiket`, `hadiah`, `tanggal_undi`, `status`) VALUES
(1, '2026-11-27 13:02:04.000000', 'Undian Akhir Tahun', 25000, 'Sepeda Motor', '2026-12-31', 'SELESAI'),
(2, '2026-11-27 13:02:04.003420', 'Undian Ramadhan', 10000, 'Paket Sembako', '2027-03-10', 'BUKA'),
(3, '2026-11-27 13:27:54.000000', 'haekal', 42000, 'PS5', '2026-11-27', 'BUKA');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `admin`
--
ALTER TABLE `admin`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- Indexes for table `pemenang`
--
ALTER TABLE `pemenang`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_pemenang_undian` (`undian_id`),
  ADD KEY `fk_pemenang_tiket` (`tiket_id`);

--
-- Indexes for table `peserta`
--
ALTER TABLE `peserta`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `tiket`
--
ALTER TABLE `tiket`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_tiket_undian` (`undian_id`),
  ADD KEY `fk_tiket_peserta` (`peserta_id`);

--
-- Indexes for table `undian`
--
ALTER TABLE `undian`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `admin`
--
ALTER TABLE `admin`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `pemenang`
--
ALTER TABLE `pemenang`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `peserta`
--
ALTER TABLE `peserta`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `tiket`
--
ALTER TABLE `tiket`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `undian`
--
ALTER TABLE `undian`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `pemenang`
--
ALTER TABLE `pemenang`
  ADD CONSTRAINT `fk_pemenang_tiket` FOREIGN KEY (`tiket_id`) REFERENCES `tiket` (`id`),
  ADD CONSTRAINT `fk_pemenang_undian` FOREIGN KEY (`undian_id`) REFERENCES `undian` (`id`);

--
-- Constraints for table `tiket`
--
ALTER TABLE `tiket`
  ADD CONSTRAINT `fk_tiket_peserta` FOREIGN KEY (`peserta_id`) REFERENCES `peserta` (`id`),
  ADD CONSTRAINT `fk_tiket_undian` FOREIGN KEY (`undian_id`) REFERENCES `undian` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
