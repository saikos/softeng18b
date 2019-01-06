# softeng2018b

Σχολή Ηλεκτρολόγων Μηχανικών & Μηχανικών Η/Υ, ΕΜΠ

Παράδειγμα web-based εφαρμογής σε Java/Groovy με χρήση Gradle για το μάθημα [Τεχνολογία Λογισμικού](http://courses.softlab.ntua.gr/softeng/2018b/), Χεμερινό εξάμηνο 2018 (softeng18b)

## Γρήγορες οδηγίες
1. Φορτώνετε το αρχείο ```src/main/sql/resources/database.sql``` στη MySQL (η βάση που χρησιμοποιείται στο παράδειγμα). Περιέχονται, ως μέρος των ζητουμένων, οι πίνακες `user`, `product`, `product_tags`, τους οποίους προσαρμόζετε κατά το δοκούν.
2. Εκτελείτε `./gradlew apprun` για να σηκώσετε το back-end. Πλοηγηθείτε στο `localhost:8765` για να δείτε απλές σελίδες (παράδειγμα είναι). Το REST API base URL είναι το `localhost:8765/observatory/api`, όπως απαιτείται από την εργασία. Για παράδειγμα, μπορείτε να δείτε τα προϊόντα στο `/products` ή το προϊόν με κωδικό 1 στο `/products/1`.
3. Εκτελείτε `./gradlew test` για να τρέξετε τα integration tests. Δείτε την κλάση [RestAPISpecification](src/test/groovy/gr/ntua/ece/softeng18b/RestAPISpecification.groovy), που περιέχει τον βασικό σκελετό των δοκιμών. 

Δείτε [εδώ](https://github.com/saikos/softeng18b-rest-api-client) τον κώδικα του πλήρως λειτουργικού (με την εξαίρεση, προς το παρόν, των τιμών) Java REST client, τον οποίο μπορείτε να χρησιμοποιήσετε για να υλοποιήσετε τα tests. Ο client θα χρησιμοποιηθεί και στην εξέταση της εργασίας.
