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

$phNa = $_POST['name'];
$pnNo = $_POST['contact'];
$IMEI = $_POST['imei'];


$sql = "INSERT INTO contacts (name, contact, imei)
VALUES ('$phNa', '$pnNo', '$IMEI');";

if ($conn->query($sql) === TRUE) {
    echo "New record created successfully";
} else {
    echo "Error: " . $sql . "<br>" . $conn->error;
}

$conn->close();
?>

name:   <?php echo $_POST["name"]; ?><br>
contact: <?php echo $_POST["contact"];?><br>
imei:   <?php echo $_POST["imei"]; ?>

</body>
</html>