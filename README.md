[BenchMark.pdf](https://github.com/user-attachments/files/23464220/BenchMark.pdf)
README.md – Benchmark de performances des Web Services REST
Pr. LACHGAR – Travail en binôme – Novembre 2025
texthttps://github.com/ton-pseudo/WebPerformance-Benchmark-REST
Objectif du projet
Évaluer, sur un même domaine métier et une même base de données, l’impact du choix de stack REST sur :

Latence (p50/p95/p99)
Débit (req/s)
Taux d’erreurs
Empreinte CPU/RAM, GC, threads
Coût d’abstraction (contrôleur « manuel » vs exposition automatique Spring Data REST)

Modèle de données (commun aux 3 variantes)
sqlCategory (id, code, name, updated_at)
Item (id, sku, name, price, stock, category_id, updated_at, description)
-- 2 000 catégories (CAT00001..CAT02000)
-- 100 000 items (~50 items/catégorie)
Variantes implémentées

























VarianteStackDescriptionA – JerseyJAX-RS (Jersey) + JPA/HibernateContrôleurs 100 % manuels, DTO, JOIN FETCH manuelC – Spring MVCSpring Boot + @RestController + Spring MVC + JPA/HibernateContrôleurs manuels avec DTO, validation Bean, JOIN FETCHD – Spring Data RESTSpring Boot + Spring Data REST (repositories exposés)Zéro contrôleur CRUD, HAL auto, relations exposées, option B RESTEasy possible
Toutes les variantes utilisent exactement la même base restbench, le même HikariCP (max=20, min=10), le même PostgreSQL 16, les mêmes indexes, aucun cache activé.
Structure du dépôt
text├── variant-a-jersey/          → Variante A
├── variant-c-restcontroller/  → Variante C  
├── variant-d-data-rest/       → Variante D (gagnante)
├── data/
│   ├── categories.csv         → 2 000 lignes
│   ├── items.csv              → 100 000 lignes
│   ├── payload_1kb.json
│   └── payload_5kb.json
├── jmeter/
│   ├── 1_READ-heavy.jmx
│   ├── 2_JOIN-filter.jmx
│   ├── 3_MIXED.jmx
│   └── 4_HEAVY-body.jmx
├── grafana/
│   └── dashboards.json        → JVM + JMeter (InfluxDB)
├── docker-compose.yml         → DB + Prometheus + InfluxDB + Grafana
├── init.sql                   → Schéma + indexes
└── Rapport_Performances_REST_LACHGAR.pdf (7 pages)
Configuration matérielle & logicielle (T0)

CPU : AMD Ryzen 5 5600X (6 cœurs / 12 threads)
RAM : 16 GB DDR4-3200
OS : Windows 11 Pro 64-bit
Java : OpenJDK 17.0.12 (Temurin)
Docker : Docker Desktop 4.35.0 + Compose v2.29.2
PostgreSQL : 16.4-alpine
JMeter : 5.6.3
Monitoring : Prometheus 2.54.1 / Grafana 11.2.0 / InfluxDB 2.7.10
JVM flags : -Xms512m -Xmx2g -XX:+UseG1GC
HikariCP : min=10 / max=20 / timeout=30s

Comment lancer le benchmark
bash# 1. Démarrer la base et le monitoring (une seule fois)
docker compose up -d postgres prometheus influxdb grafana

# 2. Générer les données (une seule fois)
python data/generate_data.py
docker exec -i postgres-benchmark psql -U postgres -d restbench < data/load_data.sql

# 3. Lancer une variante (exemple D)
cd variant-d-data-rest
mvn clean package -DskipTests
docker compose -f ../docker-compose.yml up -d variant-d

# 4. Vérifier
curl http://localhost:8083/actuator/health
curl http://localhost:8083/items?page=0&size=5

# 5. Lancer JMeter (exemple scénario 2)
jmeter -n -t jmeter/2_JOIN-filter.jmx -l result.jtl
Pour tester les autres variantes, arrêter la variante courante et lancer la suivante :
bashdocker compose down variant-d
docker compose up -d variant-c   # ou variant-a
Résultats principaux (Novembre 2025)









































CritèreGagnantValeur gagnanteÉcart vs 2ᵉDébit global (RPS moyen)D1 420 RPS+3 % vs CLatence p95 moyenneD42 ms-6 % vs CConsommation CPUD28 % moyenne-2 pt vs CHeap utiliséD720 Mo moyenne-20 Mo vs CFacilité relationnelleDHAL auto, zéro code+100 %
Conclusion officielle :
Spring Data REST (Variante D) est la grande gagnante de ce benchmark sur tous les critères techniques et surtout sur le critère décisif : exposition rapide et propre des relations avec HAL+JSON sans aucun contrôleur manuel.
Livrables inclus

Code complet des 3 variantes (A/C/D)
4 scénarios JMeter (.jmx)
Dashboards Grafana (JVM + JMeter)
Données CSV + payloads 1 KB / 5 KB
Rapport final 7 pages (T0→T7 remplis)
Ce README

Projet terminé et prêt à être présenté au Pr. LACHGAR
