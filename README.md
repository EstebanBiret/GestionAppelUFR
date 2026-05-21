# Gestion de l'appel — UT Capitole
### M2 MIAGE IPM — Projet Agile Mai 2026

---

## Prérequis

| Outil | Version |
|---|---|
| JDK | 21 |
| Maven | 3.9+ |
| Tomcat | 9.x |
---

## 1. Base de données

Créer une base de données vide nommée `gestion_appel_ufr` sur votre MySQL local (port 3306, pas de mot de passe).

> Les tables sont générées automatiquement au premier démarrage.

---

## 2. Configuration `web.xml`

Ouvrir `src/main/webapp/WEB-INF/web.xml` et renseigner les deux chemins absolus vers votre projet :

```
uploadDir      → chemin\vers\projet\src\main\webapp\images\users
uploadDirJustif → chemin\vers\projet\src\main\webapp\justifications
```

---

## 3. Lancement

```bash
mvn clean install
```

Dans IntelliJ (par ex.) : `Run > Edit Configurations > Tomcat Local > Votre version de Tomcat`  
Application context : `/ut1Appel`

Accès : **http://localhost:8080/ut1Appel**

---

## Comptes de test

| Rôle | Email | Mot de passe |
|---|---|---|
| Admin | admin@ut-capitole.fr | admin123 |
| Autres | voir `src/main/java/ut1/appel/servlet/TestServlet.java` | — |
