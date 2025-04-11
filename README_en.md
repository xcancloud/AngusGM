# AngusGM

[English](README_en.md) | [中文](README.md)

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
