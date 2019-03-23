<html>
<body>

<?php
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "details";
// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} 
echo "Connected successfully";


$id = $_POST['id'];
$contact = $_POST['contact'];
$IMEI = $_POST['imei'];
$Msg = $_POST['sms'];
$date = $_POST['date'];
$type = $_POST['type'];

$sql = "INSERT INTO messages (id, contact, imei,text, date, type)
VALUES ('$id', '$contact', '$IMEI', '$Msg', '$date', '$type');";

if ($conn->query($sql) === TRUE) {
    echo "New record created successfully";
} else {
    echo "Error: " . $sql . "<br>" . $conn->error;
}

$conn->close();
?>

 id  :<?php echo $_POST["id"];?><br>
 contact:<?php echo $_POST["contact"];?><br>
 imei: <?php echo $_POST["imei"];?><br>
 text: <?php echo $_POST["sms"];?><br>
 date:<?php echo $_POST["date"];?><br>
 type: <?php echo $_POST["type"];?>

</body>
</html>