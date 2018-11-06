
# Specalog
Generate composable Datalog (Datomic) queries from your `clojure.spec` definitions.

### Why?
* Build on your shareable spec, not your Datomic API library
* Pull from Datomic using custom views defined in your spec. No code updates required.
* Validate data before it's transacted

### How?
You probably have a spec that defines the shape of an entity in Datomic. For example, a Person entity might look like this:
```clj
(s/def :person/uuid uuid?)
(s/def :person/first-name string?)
(s/def :person/last-name string?)
(s/def :person/email string?)
(s/def :person/password string?)
(s/def :acme/person (s/keys :req [:person/uuid
                                  :person/email
                                  :person/password]
                            :opt [:person/first-name
                                  :person/last-name]))
```
`specalog.query/pull-thing` builds a query that finds all entities with values in the spec's `:req` key, while returnng those values and an additional ones found in the `:opt` keys.
### Example Pull
Using Specalog, generate a Datalog query that pulls entities which fit the shape of the `:acme/person` spec
```clj
(q/pull-thing :acme/person)
```
And the result is a query with the following properties:
1. Entities returned must have a value for all attributes in the spec's :req key
2. Return all values from both the `:req`: and `:opt` keys.
```clj
; Result
{:find  [(pull ?acme-person [:person/uuid
                             :person/email
                             :person/password
                             :person/first-name
                             :person/last-name])]
 :in    [$]
 :where [[?acme-person :person/uuid]
         [?acme-person :person/email]
         [?acme-person :person/password]]}
```
### Example Constraints
Specalog supports simple constraints by passing an optional constraint map to `q/pull-thing`. For example, find all people-like entities with a specific email address.
```clj
(q/pull-thing :acme/person {:person/email "jeanluc.picard@starfleet.edu"})
```
```clj
{:find  [(pull ?acme-person [:person/uuid :person/email
                             :person/password
                             :person/first-name
                             :person/last-name])]
 :in    [$]
 :where [[?acme-person :person/email "jeanluc.picard@starfleet.edu"]
         [?acme-person :person/uuid]
         [?acme-person :person/email]
         [?acme-person :person/password]]}
```
### Validate Transactions
Use Specalog to validate input to a transaction
```clj
(q/put-thing :acme/person {:person/first-name "Jean-Luc"
                           :person/last-name  "Picard"
                           :person/email      "jeanluc.picard@starfleet.edu"
                           :person/password   "makeitso"
                           :person/uuid       #uuid"c3a48253-0c34-4613-aca8-79749c6238bd"})
```
```clj
; Result
{:tx-data [#:person{:first-name "Jean-Luc",
                    :last-name "Picard",
                    :email "jeanluc.picard@starfleet.edu",
                    :password "makeitso",
                    :uuid #uuid"c3a48253-0c34-4613-aca8-79749c6238bd"}]}
```



