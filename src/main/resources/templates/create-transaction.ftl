<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Transaction Created</title>
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
                            Transaction Created Successfully
                        </h1>

                        <p style="margin:10px 0 0; font-size:15px;">
                            Your payment details are ready
                        </p>
                    </td>
                </tr>

                <tr>
                    <td style="padding:40px;">

                        <h2 style="margin:0; font-size:20px; color:#333; text-align:center;">
                            Hello, ${customerName}
                        </h2>

                        <p style="margin:20px 0; font-size:15px; line-height:1.6; color:#555; text-align:center;">
                            Your transaction has been created successfully.
                        </p>

                        <table width="100%" cellpadding="0" cellspacing="0" border="0"
                               style="margin-top:25px; border:1px solid #eee;
                               border-radius:10px; overflow:hidden;">

                            <tr style="background:#f9fafb;">
                                <td colspan="2"
                                    style="padding:15px; font-weight:bold; color:#333; font-size:15px;">
                                    Transaction Details
                                </td>
                            </tr>

                            <tr>
                                <td style="padding:12px; font-size:14px; color:#555;">
                                    Transaction Code
                                </td>
                                <td style="padding:12px; font-size:14px; font-weight:bold; color:#333;">
                                    ${code}
                                </td>
                            </tr>

                            <tr style="background:#f9fafb;">
                                <td style="padding:12px; font-size:14px; color:#555;">
                                    Product
                                </td>
                                <td style="padding:12px; font-size:14px; font-weight:bold; color:#333;">
                                    ${productName}
                                </td>
                            </tr>

                            <tr>
                                <td style="padding:12px; font-size:14px; color:#555;">
                                    Payment Gateway
                                </td>
                                <td style="padding:12px; font-size:14px; font-weight:bold; color:#333;">
                                    ${paymentGatewayName}
                                </td>
                            </tr>

                            <tr style="background:#f9fafb;">
                                <td style="padding:12px; font-size:14px; color:#555;">
                                    Virtual Account Number
                                </td>
                                <td style="padding:12px; font-size:15px; font-weight:bold; color:#4facfe;">
                                    ${virtualAccountNumber}
                                </td>
                            </tr>

                            <tr>
                                <td style="padding:12px; font-size:14px; color:#555;">
                                    Status
                                </td>
                                <td style="padding:12px; font-size:14px; font-weight:bold; color:green;">
                                    ${transactionStatusName}
                                </td>
                            </tr>

                            <tr style="background:#f9fafb;">
                                <td style="padding:12px; font-size:14px; color:#555;">
                                    Admin Fee
                                </td>
                                <td style="padding:12px; font-size:14px; font-weight:bold; color:#333;">
                                    Rp ${adminRate}
                                </td>
                            </tr>

                            <tr>
                                <td style="padding:12px; font-size:14px; color:#555;">
                                    Total Payment
                                </td>
                                <td style="padding:12px; font-size:16px; font-weight:bold; color:#d9534f;">
                                    Rp ${totalPrice}
                                </td>
                            </tr>

                            <tr style="background:#f9fafb;">
                                <td style="padding:12px; font-size:14px; color:#555;">
                                    Date
                                </td>
                                <td style="padding:12px; font-size:14px; font-weight:bold; color:#333;">
                                    ${createdAt}
                                </td>
                            </tr>

                        </table>

                        <p style="margin-top:30px; font-size:13px; color:#777; text-align:center;">
                            Thank you for purchasing through our service.<br/>
                            Please wait while the transaction is being processed by our system.
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
