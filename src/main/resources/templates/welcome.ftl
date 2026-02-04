<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Activate Your Account</title>
</head>

<body style="margin:0; padding:0; background-color:#f4f6f8; font-family:Arial, sans-serif;">

<table width="100%" cellpadding="0" cellspacing="0" border="0"
       style="background-color:#f4f6f8; padding:30px 0;">
    <tr>
        <td align="center">

            <table width="600" cellpadding="0" cellspacing="0" border="0"
                   style="background:#ffffff; border-radius:12px;
                   overflow:hidden; box-shadow:0 4px 12px rgba(0,0,0,0.08);">

                <tr>
                    <td align="center"
                        style="background:linear-gradient(135deg,#4facfe,#00f2fe);
                        padding:30px; color:white;">

                        <h1 style="margin:0; font-size:26px; font-weight:bold;">
                            Welcome to Lipayz
                        </h1>

                        <p style="margin:10px 0 0; font-size:15px;">
                            Your journey starts here.
                        </p>
                    </td>
                </tr>

                <tr>
                    <td style="padding:40px; text-align:center;">

                        <h2 style="margin:0; font-size:20px; color:#333;">
                            Hi, <span th:text="${fullName}">Customer</span>
                        </h2>

                        <p style="margin:20px 0; font-size:15px; line-height:1.6; color:#555;">
                            Thank you for signing up with <b>Lipayz</b>.
                            To start using your account, please confirm your email address by clicking the button below.
                        </p>

                        <a th:href="${activationLink}"
                           style="display:inline-block;
                           padding:14px 28px;
                           background:#4facfe;
                           color:#ffffff;
                           text-decoration:none;
                           border-radius:8px;
                           font-size:16px;
                           font-weight:bold;">
                            Activate Account
                        </a>

                        <p style="margin-top:25px; font-size:13px; color:#777;">
                            If the button doesn’t work, copy and paste this link into your browser:
                        </p>

                        <p style="font-size:12px; word-break:break-all; color:#4facfe;">
                            <span th:text="${activationLink}"></span>
                        </p>

                    </td>
                </tr>

                <tr>
                    <td align="center"
                        style="background:#f9fafb; padding:20px; font-size:12px; color:#888;">

                        <p style="margin:0;">
                            Need help? Contact us at
                            <a href="mailto:support@lipayz.com"
                               style="color:#4facfe; text-decoration:none;">
                                support@lipayz.com
                            </a>
                        </p>

                        <p style="margin:10px 0 0;">
                            © 2026 Lipayz. All rights reserved.
                        </p>

                    </td>
                </tr>

            </table>
        </td>
    </tr>
</table>

</body>
</html>
