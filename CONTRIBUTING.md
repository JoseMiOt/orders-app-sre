# Guía de contribución

¡Gracias por tu interés en contribuir a Orders App! 🙌  
Este documento describe las buenas prácticas para colaborar en el proyecto.

---

## 🧩 Flujo de trabajo para contribuir

1. Haz un **fork** del repositorio.
2. Crea una **rama nueva** para tu cambio:

   ```bash
   git checkout -b feature/mi-mejora

3. Realiza tus cambios en código.

4. Asegúrate de que el proyecto buildé correctamente:

    mvn clean package


5. Verifica que la aplicación sigue funcionando en Kubernetes:

    kubectl get pods -A


6. Haz commit con un mensaje claro:

    git commit -m "Agrego nueva métrica/funcion"


7. Sube tu rama:

    git push origin feature/mi-mejora


8. Abre un Pull Request, explicando:

    Qué problema resuelve
    Qué cambios hiciste
    Cómo probarlos