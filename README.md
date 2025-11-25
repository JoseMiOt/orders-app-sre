# 📦 Orders App – Proyecto SRE con Kubernetes, Prometheus, Grafana y Alertmanager

![status](https://img.shields.io/badge/status-completed-brightgreen)
![kubernetes](https://img.shields.io/badge/Kubernetes-Enabled-blue)
![grafana](https://img.shields.io/badge/Grafana-Dashboard-orange)
![prometheus](https://img.shields.io/badge/Prometheus-Monitoring-orange)
![alertmanager](https://img.shields.io/badge/Alertmanager-Slack_Alerts-blueviolet)
![slack](https://img.shields.io/badge/Slack-Integrated-blue)
![docker](https://img.shields.io/badge/Docker-Containerized-2496ED?logo=docker&logoColor=white)
![java](https://img.shields.io/badge/Java-21-orange?logo=openjdk&logoColor=white)
![springboot](https://img.shields.io/badge/Spring_Boot-3.x-brightgreen?logo=springboot&logoColor=white)
![license](https://img.shields.io/badge/License-MIT-yellow)


Aplicación de ejemplo para gestionar órdenes y productos, desarrollada en **Spring Boot**, desplegada en **Kubernetes**, monitoreada con **Prometheus**, visualizada mediante **Grafana** y con alertas enviadas a **Slack** utilizando **Alertmanager**.

Este proyecto forma parte de mi proyecto final de la Academia SRE/Observabilidad.

---

## 📋 Tabla de Contenidos

1. [Descripción General](#descripción-general)
2. [Arquitectura](#arquitectura)
3. [Stack Tecnológico](#stack-tecnológico)
4. [Estructura del Proyecto](#estructura-del-proyecto)
5. [Ejecución Local](#ejecución-local)
6. [Despliegue en Kubernetes](#despliegue-en-kubernetes)
   - [Namespaces](#namespaces)
   - [Orders App](#orders-app)
   - [Prometheus](#prometheus)
   - [Grafana](#grafana)
   - [Alertmanager](#alertmanager)
7. [Métricas y Alertas](#métricas-y-alertas)
8. [Dashboard en Grafana](#dashboard-en-grafana)
9. [Endpoints de la API](#endpoints-de-la-api)
10. [Capturas de Pantalla](#capturas-de-pantalla)
11. [Cómo Contribuir](#cómo-contribuir)
12. [Licencia](#licencia)

---


## 🧾 Descripción General

`Orders App` es una aplicación REST desarrollada en Spring Boot que expone endpoints para gestionar órdenes y productos.  
Está instrumentada con Micrometer y Actuator para exponer métricas en formato Prometheus.

### Funcionalidades:
- Crear órdenes  
- Ver órdenes  
- Eliminar órdenes  
- Ver productos predefinidos   

La aplicación expone métricas en el endpoint:
/actuator/prometheus


Prometheus las lee, Grafana las visualiza y Alertmanager envía alertas a Slack.

---

## 🏗️ Arquitectura

| Componente      | Función |
|-----------------|---------|
| **Orders App**  | Expone métricas en `/actuator/prometheus` vía Spring Boot + Actuator |
| **Prometheus**  | Scrapea métricas, aplica `alert-rules.yml` y envía alertas a Alertmanager |
| **Alertmanager**| Procesa reglas, agrupa alertas y las envía a un Webhook de Slack |
| **Slack**       | Recibe alertas en el canal `#alerts` |
| **Grafana**     | Consume Prometheus como Data Source y muestra dashboards personalizados |

---

# 🧰 ⚙️ Stack Tecnológico

- ☕ **Java 21**
- 🌱 **Spring Boot**
- 📊 **Micrometer + Actuator**
- 🐳 **Docker**
- ☸️ **Kubernetes (Minikube)**
- 🔭 **Prometheus**
- 📈 **Grafana**
- 🚨 **Alertmanager**
- 🔗 **Slack Webhooks**

---

# 📁 Estructura del Proyecto

orders-app/
│── manifests/
│ ├── prometheus-configmap.yaml
│ ├── prometheus-deployment.yaml
│ ├── grafana-deployment.yaml
│ ├── alertmanager-configmap.yaml
│ ├── alertmanager-deployment.yaml
│ └── alert-rules.yaml
│
├── src/main/java/com/ordersystem/ordersapp/
│ ├── controller/
│ ├── model/
│ └── service/
│
├── docs/screenshots/
│── README.md
│── CONTRIBUTING.md
│── LICENSE
├── Dockerfile
├── pom.xml


---

# 🚀 Ejecución Local

mvn clean package
java -jar target/orders-app.jar

📌 La app queda disponible en:
👉 http://localhost:8081

---
# ☸️ Despliegue en Kubernetes
## 🧱 1. Crear Namespaces
kubectl create namespace orders-system
kubectl create namespace monitoring

## 📦 2. Desplegar Orders App
kubectl apply -f manifests/orders-app.yaml

Verificar:

kubectl get pods -n orders-system
kubectl get svc -n orders-system

## 📊 3. Desplegar Prometheus
kubectl apply -f manifests/prometheus-configmap.yaml
kubectl apply -f manifests/prometheus-deployment.yaml
kubectl apply -f manifests/prometheus-service.yaml

Port-forward:

kubectl port-forward svc/prometheus -n monitoring 9090:9090

🔗 URL: http://localhost:9090

## 📈 4. Desplegar Grafana
kubectl apply -f manifests/grafana-deployment.yaml
kubectl apply -f manifests/grafana-service.yaml

Port-forward:
kubectl port-forward svc/grafana -n monitoring 3000:3000

🔗 URL: http://localhost:3000
👤 Usuario: admin
🔐 Contraseña: admin

## 🔔 5. Desplegar Alertmanager
kubectl apply -f manifests/alertmanager-configmap.yaml
kubectl apply -f manifests/alertmanager-deployment.yaml
kubectl apply -f manifests/alertmanager-service.yaml

Port-forward:
kubectl port-forward svc/alertmanager -n monitoring 9093:9093

🔗 URL: http://localhost:9093

## 📡 6. Reglas de Alerta
kubectl apply -f manifests/alert-rules.yaml

El Webhook de Slack se configuró en: alertmanager-configmap.yaml

## 📊 Métricas y Alertas

### 🔔 Alertas Configuradas

#### 🚨 OrdersAppDown  
Se dispara cuando el servicio está caído:


up{job="orders-app"} == 0

## ⚠️ HighRestartCount
Detecta reinicios anormales en el pod:

increase(kube_pod_container_status_restarts_total[5m]) > 1

## 📈 Dashboard en Grafana

El dashboard incluye:

🟢 Estado UP/DOWN del servicio
🚀 Latencia por endpoint
🛒 Órdenes creadas
📦 Productos vendidos
❌ Errores en las últimas 24h
📉 Disponibilidad (SLO)
📊 Tendencias y distribución de ventas

Además, se incluye el JSON exportado del dashboard para importación rápida:

📁 /docs/grafana-dashboard.json


# 🧪 Endpoints de la API

A continuación algunos ejemplos básicos de los endpoints expuestos por la aplicación:

## 📦 Productos
GET /products

## 📝 Crear órden
POST /orders
Body: [1,2]

## 📄 Ver órdenes
GET /orders

## ❌ Eliminar orden
DELETE /orders/{id}

## 🖼️ Capturas de Pantalla
Todas las imágenes se encuentran en:
/docs/screenshots/

Incluyen evidencia de:

- 📡 **Prometheus** (targets UP/DOWN, alertas)
- 📊 **Grafana** (dashboard principal + datasource)
- ☸️ **Kubernetes** (pods, services)
- 🚨 **Alertmanager** (estado y alertas enviadas a Slack)
- 🔗 **Webhook de Slack** recibiendo alertas
- 📝 **Export del dashboard** en JSON

## 🤝 Cómo Contribuir  
Consulta el archivo: **[CONTRIBUTING.md](CONTRIBUTING.md)**


## 📄 Licencia  
Este proyecto utiliza la licencia **MIT**.  
Puedes verla en el archivo: **[LICENSE](LICENSE)**
