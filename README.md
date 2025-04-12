# AngusGM

[English](README_en.md) | [中文](README.md)

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.0-brightgreen)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-4.2.0-brightgreen)](https://spring.io/projects/spring-cloud)
[![Eureka Client](https://img.shields.io/badge/Eureka%20Client-2.0.4-lightgrey)](https://spring.io/projects/spring-cloud-netflix)
[![Angus Infra](https://img.shields.io/badge/Angus%20Infra-1.0.0-red)](https://github.com/xcancloud/AngusInfra)
[![Open API](https://img.shields.io/badge/Open%20API-3.0.1-blue)](https://swagger.io/specification/)

**Angus全局管理** (Angus Global Management，简称 **AngusGM**) 是为系统管理人员设计的统一系统资源管理平台，旨在为Angus系列应用提供基础能力支持，实现跨业务系统的高效协同与资源整合。

## 主要功能

- **组织管理**  
  管理企业组织架构、资源分配与规划，支持多层级部门与团队协作，确保组织内部高效运作。

- **全局权限**  
  统一管理应用权限策略与用户权限分配，支持基于角色的访问控制（RBAC），保障系统安全性与一致性。

- **公告与消息**  
  提供全系统公告发布、个性化消息推送功能，支持实时通知与历史消息追溯，确保信息触达精准及时。

- **系统管理**  
  配置系统参数、审计日志、安全策略及备份恢复，保障系统稳定性与数据安全性。

## 部署与集成

- **私有化部署**  
  支持本地化或私有云环境部署，提供Docker镜像与Kubernetes Helm Chart，一键完成安装与初始化。

- **模块化扩展**  
  作为基础核心模块，其他业务应用（如AngusTester、AngusSeek等）可复用AngusGM的权限、组织等能力，减少重复配置。

## 注意事项

- **依赖环境**：需预装JDK 17+、MySQL 7.0+ 或 PostgreSQL 11+。
- **兼容性**：AngusGM v1.0+ 仅支持与Angus系列应用v1.0+集成。

## 开源协议

📜 本项目采用 [GPLv3](https://www.gnu.org/licenses/gpl-3.0.html) 开源协议。
