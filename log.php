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


$PhNo = $_POST['phonenumber'];
$CallDur = $_POST['callduration'];
$CallType = $_POST['calltype'];
$CallDate = $_POST['calldate'];
$IMEI = $_POST['imei'];

$sql = "INSERT INTO call_logs (phonenumber,callduration,calltype,calldate,imei)
VALUES ('$PhNo', '$CallDur', '$CallType', '$CallDate', '$IMEI');";

if ($conn->query($sql) === TRUE) {
    echo "New record created successfully";
} else {
    echo "Error: " . $sql . "<br>" . $conn->error;
}

$conn->close();
?>

phonenumber:   <?php echo $_POST["phonenumber"]; ?><br>
callduration: <?php echo $_POST["callduration"];?><br>
calltype: <?php echo $_POST["calltype"];?><br>
calldate: <?php echo $_POST["calldate"];?><br>
imei:   <?php echo $_POST["imei"]; ?>

</body>
</html>