<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <title>Account Activated</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background: #f4f6f8;
            text-align: center;
            padding: 60px;
            margin: 0;
        }
        .card {
            max-width: 500px;
            margin: auto;
            background: #ffffff;
            padding: 40px;
            border-radius: 12px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
        }
        h1 {
            color: #28a745;
            margin-bottom: 15px;
        }
        p {
            color: #555;
            font-size: 15px;
            line-height: 1.6;
        }
        a {
            display: inline-block;
            margin-top: 25px;
            padding: 12px 25px;
            background: #4facfe;
            color: #ffffff;
            text-decoration: none;
            border-radius: 8px;
            transition: background 0.3s;
        }
        a:hover {
            background: #2f8fe6;
        }
    </style>
</head>
<body>

<div class="card">
    <h1>Account Activated!</h1>

    <p>
        ${message}
    </p>

    <a href="http://localhost:8080/auth/login">
        Go to Login
    </a>
</div>

</body>
</html>
