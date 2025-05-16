# AngusGM

[English](README.md) | [中文](README_zh.md)

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.0-brightgreen)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-4.2.0-brightgreen)](https://spring.io/projects/spring-cloud)
[![Eureka Client](https://img.shields.io/badge/Eureka%20Client-2.0.4-lightgrey)](https://spring.io/projects/spring-cloud-netflix)
[![Angus Infra](https://img.shields.io/badge/Angus%20Infra-1.0.0-red)](https://github.com/xcancloud/AngusInfra)
[![Open API](https://img.shields.io/badge/Open%20API-3.0.1-blue)](https://swagger.io/specification/)

**Angus Global Management** (**AngusGM**) is a unified system resource management platform designed for administrators, serving as the foundational module for the Angus application suite to enable cross-system collaboration and resource integration.

## Key Features

- **Organization Management**  
  Manage enterprise hierarchy, resource allocation, and multi-level department/team collaboration for operational efficiency.

- **Global Permissions**  
  Centralized RBAC-based permission management for applications and users, ensuring security and consistency.

- **Announcements & Messaging**  
  Publish system-wide announcements and personalized notifications with real-time delivery and historical tracking.

- **System Administration**  
  Configure parameters, audit logs, security policies, and backup/recovery to ensure system stability and data integrity.

## Deployment & Integration

- **Private Deployment**  
  Supports on-premises or private cloud deployment via Docker images and Kubernetes Helm Charts for one-click setup.

- **Modular Extensions**  
  As a core module, other Angus applications (e.g., AngusTester, AngusSeek) can reuse AngusGM's permissions and organizational capabilities.

## Notes

- **Dependencies**: Requires JDK 17+, MySQL 7.0+, or PostgreSQL 11+.
- **Compatibility**: AngusGM v1.0+ is compatible only with Angus suite applications v1.0+.

## License

📜 Licensed under [GPLv3](https://www.gnu.org/licenses/gpl-3.0.html).
