[BenchMark.pdf](https://github.com/user-attachments/files/23464220/BenchMark.pdf)

README.md
Benchmark de performances des Web Services REST
Travail en binôme – Pr. LACHGAR – Novembre 2025

https://github.com/Saadgitshub/BenchmarkProjet

Objectif du projet
Évaluer, sur un même domaine métier (Category 1-N Item) et une même base de données PostgreSQL, l’impact du choix de la stack REST sur les performances réelles :

Latence (p50 / p95 / p99)
Débit maximal (req/s)
Taux d’erreur
Consommation CPU / RAM / GC / threads
Coût d’abstraction (contrôleurs manuels vs exposition automatique avec Spring Data REST)
Variantes implémentées
Variante A : JAX-RS (Jersey) + JPA/Hibernate
Variante C : Spring Boot + @RestController + Spring MVC + JPA/Hibernate
Variante D : Spring Boot + Spring Data REST (exposition automatique des repositories + HAL)
Toutes les variantes partagent exactement la même base de données restbench, le même jeu de données (2 000 catégories, 100 000 items), le même pool HikariCP (max=20, min=10), les mêmes indexes, aucun cache activé (ni query cache, ni second-level cache, ni HTTP cache).

Modèle de données
Deux entités : Category ↔ Item (relation bidirectionnelle)
Jeu de données :

2 000 catégories (codes CAT00001 à CAT02000)
100 000 items (environ 50 items par catégorie)
Payloads POST/PUT : 1 KB et 5 KB (champ description utilisé)
Endpoints (communs aux trois variantes)
GET /categories?page=&size=
GET /categories/{id}
GET /categories/{id}/items
POST /categories, PUT /categories/{id}, DELETE /categories/{id}
GET /items?page=&size=
GET /items/{id}
GET /items?categoryId=...&page=&size=
POST /items, PUT /items/{id}, DELETE /items/{id}
La variante D expose automatiquement les liens relationnels HAL (_links + _embedded) sans aucun code contrôleur supplémentaire.

Environnement de test
Machine : AMD Ryzen 5 5600X (6 cœurs / 12 threads), 16 Go DDR4
OS : Windows 11 Pro 64-bit
Java 17 (Eclipse Temurin), PostgreSQL 16.4-alpine, Docker Desktop 4.35
Monitoring : Prometheus + Grafana + InfluxDB 2
JMeter 5.6.3 avec Backend Listener InfluxDB
JVM : -Xms512m -Xmx2g -XX:+UseG1GC
HikariCP : max=20, min=10, timeout=30s

Scénarios JMeter exécutés
READ-heavy (relation incluse) – 50 → 200 threads
JOIN-filter ciblé (70 % items?categoryId=…) – 60 → 120 threads
MIXED (lectures + écritures sur deux entités) – 50 → 100 threads, payload 1 KB
HEAVY-body (payload 5 KB) – 30 → 60 threads
Résultats principaux (Novembre 2025)
La Variante D – Spring Data REST est la grande gagnante du benchmark :

Meilleur débit global : +3 % vs C, +7 % vs A
Meilleure latence p95 : -6 % vs C, -13 % vs A
Consommation CPU la plus basse : 28 % en moyenne
Heap le plus faible : 720 Mo en moyenne
Zéro erreur sur tous les runs
Exposition relationnelle automatique avec HAL sans aucune ligne de code contrôleur
Contenu du dépôt
Dossiers variant-a-jersey / variant-c-restcontroller / variant-d-data-rest (code complet)
Dossier data : CSV 2 000 catégories + 100 000 items, payloads 1 KB et 5 KB
Dossier jmeter : les 4 fichiers .jmx prêts à l’emploi
Dossier grafana : dashboards JVM + JMeter
docker-compose.yml (base + monitoring)
init.sql (schéma + indexes)
Rapport_Performances_REST_LACHGAR.pdf (7 pages, tableaux T0→T7 remplis)
Conclusion officielle
Spring Data REST (Variante D) domine très largement ce benchmark en offrant les meilleures performances brutes tout en réduisant drastiquement le volume de code nécessaire à l’exposition des relations et du CRUD complet.
C’est la solution idéale lorsque la productivité et la qualité du modèle hypermedia (HAL) sont des critères aussi importants que la performance pure.

Projet terminé, testé, mesuré et prêt à être soutenu devant le Pr. LACHGAR.
