~/maven/bin/mvn -DskipTests clean dependency:list install
export DATABASE_URL="jdbc:mysql://localhost:3306/booking?reconnect=true&user=booking&password=Heroku"
java -jar target/booking-0.0.1.jar
