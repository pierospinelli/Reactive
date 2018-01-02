cd C:\KARAF\apache-karaf-4.2.0.M1
cd etc
mkdir keystore
cd keystore
keytool -genkeypair -keyalg RSA -validity 2048 -alias dontesta-karaf -dname "cn=karaf.pjsoft.it, ou=PJSoft Consulting, o=PJSoft Reactive, C=IT, L=Rome, S=Italy" -keypass pjsoft -storepass pjsoft -keystore pjsoft-karaf-server.jks -ext SAN=DNS:www.pjsoftconsulting.it,DNS:services.pjsoftconsulting.it