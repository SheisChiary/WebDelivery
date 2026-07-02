<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Registrazione - WebDelivery</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="css/style.css">
</head>
<body class="auth-body">
    
    <header class="auth-header">
        <div class="logo"><i class="fa-solid fa-utensils"></i> WebDelivery</div>
    </header>

    <div class="auth-wrapper">
        <div class="auth-card">
            <h2 class="auth-title">Benvenuto!</h2>
            <p class="auth-subtitle">Crea il tuo account per iniziare a ordinare.</p>

            <#if errore??>
                <div class="alert alert-danger">${errore}</div>
            </#if>

            <form action="registrazione" method="POST">
                
                <div class="form-group">
                    <label for="nomeCompleto">Nome Completo</label>
                    <input type="text" id="nomeCompleto" name="nomeCompleto" required placeholder="es. Mario Rossi">
                </div>

                <div class="form-group">
                    <label for="email">Email</label>
                    <input type="email" id="email" name="email" required placeholder="es. mario@email.it">
                </div>

                <div class="form-group">
                    <label for="telefono">Numero di Telefono</label>
                    <input type="tel" id="telefono" name="telefono" required placeholder="es. 333 1234567">
                </div>

                <div class="form-group">
                    <label for="indirizzo">Via di Casa</label>
                    <input type="text" id="indirizzo" name="indirizzo" required placeholder="es. Via Roma 1, Milano">
                </div>
                
                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" required placeholder="Crea una password sicura">
                </div>
                
                <button type="submit" class="btn-auth">Registrati</button>
            </form>

            <p class="auth-footer">Hai già un account? <a href="login">Accedi ora</a></p>
        </div>
    </div>
</body>
</html>