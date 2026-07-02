<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Login - WebDelivery</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="css/style.css">
</head>
<body class="auth-body">
    
    <header class="auth-header">
        <div class="logo"><i class="fa-solid fa-utensils"></i> WebDelivery</div>
    </header>

    <div class="auth-wrapper">
        <div class="auth-card">
            <h2 class="auth-title">Bentornato!</h2>
            <p class="auth-subtitle">Inserisci le tue credenziali per accedere.</p>

            <#if errore??>
                <div class="alert alert-danger">${errore}</div>
            </#if>

            <form action="login" method="POST">
                <div class="form-group">
                    <label for="email">Email</label>
                    <input type="email" id="email" name="email" required placeholder="es. mario@email.it">
                </div>
                
                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" required placeholder="Inserisci la tua password">
                </div>
                
                <button type="submit" class="btn-auth">Accedi</button>
            </form>

            <p class="auth-footer">Non hai un account? <a href="registrazione">Registrati ora</a></p>
        </div>
    </div>
</body>
</html>