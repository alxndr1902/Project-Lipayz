<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Account Activated</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background: #f4f6f8;
            text-align: center;
            padding: 60px;
        }
        .card {
            max-width: 500px;
            margin: auto;
            background: white;
            padding: 40px;
            border-radius: 12px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
        }
        h1 {
            color: #28a745;
        }
        p {
            color: #555;
        }
        a {
            display: inline-block;
            margin-top: 25px;
            padding: 12px 25px;
            background: #4facfe;
            color: white;
            text-decoration: none;
            border-radius: 8px;
        }
    </style>
</head>
<body>

<div class="card">
    <h1>Account Activated!</h1>

    <p th:text="${message}">
        Your account has been successfully activated.
    </p>

    <a href="http://localhost:8080/auth/login">
        Go to Login
    </a>
</div>

</body>
</html>
