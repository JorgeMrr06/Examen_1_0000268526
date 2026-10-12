# Publicacion del proyecto Java

Este repositorio presenta todos los archivos Java en la raiz para permitir su carga desde GitHub Web. Cada archivo mantiene su declaracion original de paquete y su contenido. Los scripts de esta raiz compilan todos los archivos con Java 17 y generan los directorios de paquetes mediante javac -d build/classes.

En Windows: build.cmd compila; probar.cmd ejecuta las pruebas; demo.cmd genera evidencia; ejecutar.cmd abre la aplicacion. En Linux/macOS usa los scripts equivalentes con sh. Los JAR compilados se crean en dist/.

**Proyecto_Java_CU6.zip** incluye la estructura convencional original src/ y test/, los scripts originales y el JAR runnable. Puedes descargarlo y descomprimirlo para trabajar en un IDE. La documentacion que sigue describe ese proyecto convencional; las mismas clases y reglas funcionan en la version visible de este repositorio.

---
# CU6: Comprar producto

**Luis Antonio Cota Rea · ITSON 0000268526**

Aplicación de escritorio en Java Swing para el caso de uso 6 del examen. El catálogo muestra nombre y costo; cada selección se incorpora a la lista de la derecha. Los datos de tarjeta se presentan en la zona inferior, y el ticket se genera debajo de esos datos en la misma zona cuando se selecciona Pagar.

La aplicación simula una compra con información local y ficticia. No utiliza red, base de datos, SDK bancario ni cobra dinero.

## Ejecutar

Necesitas **Java 17 o superior**. Para compilar necesitas el **JDK**, que incluye `javac` y `jar`.

En Windows, abre `ejecutar.cmd`, o desde una terminal en esta carpeta:

```bat
ejecutar.cmd
```

El JAR ya compilado está en `dist/cu6-comprar-producto.jar`. También puedes ejecutarlo directamente:

```sh
java -jar dist/cu6-comprar-producto.jar
```

En Linux o macOS:

```sh
sh ejecutar.sh
```

La ventana se ajusta al área disponible de la pantalla. Las tablas y el ticket tienen barras de desplazamiento cuando es necesario.

## Compilar y verificar

La compilación produce bytecode compatible con Java 17 y no descarga dependencias.

En Windows:

```bat
build.cmd
probar.cmd
demo.cmd
```

`build.cmd` utiliza PowerShell para resolver el JDK del comando `java`; esto también funciona con la instalación de Oracle que expone `java` y `javac` mediante `javapath`. Si tu equipo usa un JRE o una versión anterior a 17, agrega el directorio `bin` de un JDK 17 o superior al `PATH` antes de ejecutar el script.

En Linux o macOS:

```sh
sh build.sh
sh probar.sh
sh demo.sh
```

`probar` ejecuta **45 verificaciones** de flujo básico, reglas de negocio y coordinación MVC. `demo` usa eventos de botones y selección de componentes Swing en el hilo EDT; dibuja la interfaz real con `printAll` y guarda cuatro imágenes de storyboard, once estados intermedios, el ticket y un registro de la ejecución en `build/evidencia`. No toma fotografías del escritorio ni requiere una pantalla para las pruebas.

## Demostración del flujo básico

1. Selecciona **Cuaderno profesional**, indica **2** unidades y pulsa **Agregar a la selección**. Los dos cuadernos aparecen a la derecha.
2. Selecciona **Memoria USB 32 GB**, indica **1** unidad y agrega el producto. El carrito muestra dos filas y un total de **$249.90 MXN**.
3. Ingresa **4111 1111 1111 1111** en el campo inferior y pulsa **Consultar tarjeta**. Aparecen Banco del Valle, Hermosillo y el número terminado en 1111.
4. Pulsa **Pagar y generar ticket**. El ticket aparece debajo de los datos de tarjeta con las líneas de compra y el total. El inventario queda en 18 cuadernos y 9 memorias USB.
5. La compra queda bloqueada para impedir un segundo pago. **Nueva compra** limpia el carrito y la tarjeta y conserva el inventario restante de esa ejecución.

## Datos ficticios

| Producto | Costo MXN | Inventario inicial |
|---|---:|---:|
| Cuaderno profesional | $45.00 | 20 |
| Memoria USB 32 GB | $159.90 | 10 |
| Mochila escolar | $499.00 | 5 |
| Bolígrafo azul | $12.50 | 40 |
| Termo de acero | $229.00 | 8 |

| Número de prueba | Banco emisor ficticio | Ciudad |
|---|---|---|
| 4111111111111111 | Banco del Valle | Hermosillo |
| 5555555555554444 | Banco del Pacífico | Ciudad Obregón |
| 4000000000000002 | Banco del Norte | Navojoa |

El campo de entrada es `JPasswordField`. En los datos mostrados y en el ticket se conservan únicamente los últimos cuatro dígitos: `•••• •••• •••• 1111`. Esta regla sigue representando el número de tarjeta solicitado y evita mostrarlo completo. Los números completos de esta tabla son exclusivamente datos de demostración.

## Reglas y decisiones

- Se utilizan `BigDecimal` y precios decimales exactos; no se usa `double` para dinero.
- La cantidad debe ser positiva y no superar el inventario. Selecciones repetidas del mismo producto se acumulan en una fila; el límite considera la cantidad acumulada.
- Se puede retirar una fila del carrito antes de pagar.
- La tarjeta debe tener 16 dígitos, pasar el control local de Luhn y estar en el directorio ficticio. Se aceptan espacios y guiones en la captura. Un número que sólo pase Luhn no es suficiente.
- Consultar tarjeta muestra banco, ciudad y número. Pagar vuelve a validar la tarjeta, incluso si ya fue consultada. También es posible pagar directamente después de capturarla.
- Todos los datos se validan antes de descontar stock. Un pago fallido no crea ticket ni modifica inventario.
- Después del pago se bloquean la edición y los botones de pago. El modelo también rechaza una segunda llamada a `pagar`.
- Los costos están en MXN. No se añaden impuestos, envío ni comisiones porque el caso de uso no los especifica.
- Los datos viven en memoria. Cerrar la aplicación restablece catálogo e inventario para la siguiente ejecución. La fecha del ticket utiliza la zona `America/Hermosillo`.

## Arquitectura MVC y coherencia con UML

`edu.itson.cu6.Main` conecta los tres componentes y abre la ventana.

| Capa | Clases | Responsabilidad |
|---|---|---|
| Modelo | `CompraModel`, `Producto`, `LineaCompra`, `Tarjeta`, `Ticket` | Datos de dominio, cantidades, total, validación, inventario y ticket |
| Vista | `CompraView`, `SwingCompraView` | Contrato de presentación e interfaz de escritorio |
| Controlador | `CompraController` | Recibir acciones, invocar el modelo y actualizar la vista |

La vista envía eventos al controlador. El controlador depende del contrato `CompraView`, y el modelo no importa Swing. Las pruebas del controlador emplean una vista de prueba para comprobar las actualizaciones sin abrir una ventana.

El flujo de diseño utiliza métodos del código: `iniciar` → `listarProductos` → `mostrarProductos`; `agregarProducto` → `getCarrito`/`getTotal` → `mostrarCarrito`; `consultarTarjeta` → `mostrarTarjeta`; `pagar` → creación de `Ticket` y `descontarStock` → `mostrarTarjeta`/`mostrarTicket`/`mostrarProductos`/`bloquearCompra`.

El proyecto es independiente del caso de uso 4. El archivo `Examen_1_0000268526.mdj` y el PDF del paquete describen este caso de uso y la misma implementación.

## Casos alternos verificados

La suite valida carrito vacío; producto no seleccionado o inexistente; cantidades cero, negativas, excesivas y acumulación muy grande; tarjeta vacía, nula, con letras, con Luhn incorrecto o sin registro; pago fallido sin cambios; acumulación de selecciones; total exacto; consulta de las tres tarjetas; ticket y lista inmutable; descuento único de stock; intento de pago repetido; prohibición de editar compra pagada; retiro de productos; inicio de otra compra y actualización correcta de la vista.

Para una revisión manual, intenta seleccionar seis mochilas (sólo hay cinco), modifica el último dígito de la tarjeta de prueba y trata de pagar sin productos. Los errores aparecen en rojo en la parte inferior de la ventana.
