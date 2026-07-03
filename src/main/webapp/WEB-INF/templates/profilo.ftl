<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>WebDelivery - Profilo</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <div class="dash-container">
        <aside class="dash-sidebar">
            <div class="dash-logo"><i class="fa-solid fa-utensils"></i> WebDelivery</div>
            <ul class="dash-nav">
                <li><a href="menu"><i class="fa-solid fa-arrow-left"></i> Torna al Menù</a></li>
                <li><a href="profilo" class="active"><i class="fa-solid fa-user"></i> Il mio Profilo</a></li>
                <li><a href="storico-ordini"><i class="fa-solid fa-clock-rotate-left"></i> I Miei Ordini</a></li>
            </ul>
            <ul class="dash-nav" style="flex-grow: 0;">
                <li><a href="logout" class="danger"><i class="fa-solid fa-right-from-bracket"></i> Esci dall'account</a></li>
            </ul>
        </aside>

        <main class="dash-main">
            <header class="dash-header">
                <h1>Impostazioni Profilo</h1>
                <p>Gestisci i tuoi dati personali e gli indirizzi di consegna.</p>
            </header>

            <div class="dash-card">
                <#if successMessage??>
                    <div class="msg-success">
                        <i class="fa-solid fa-circle-check" style="font-size: 1.2rem;"></i> ${successMessage}
                    </div>
                </#if>

                <form action="profilo" method="POST">
                    <div class="form-row">
                        <label>Nome Completo</label>
                        <input type="text" name="nomeCompleto" value="${utente.nomeCompleto!''}" required>
                    </div>
                    
                    <div class="form-row">
                        <label>Indirizzo Email</label>
                        <input type="email" value="${utente.email!''}" disabled>
                    </div>
                    
                    <div class="form-row">
                        <label>Numero di Telefono</label>
                        <input type="text" name="telefono" value="${utente.telefono!''}" required>
                    </div>
                    
                    <div class="form-row">
                        <label>Indirizzo di Consegna Predefinito</label>
                        <input type="text" name="indirizzo" value="${utente.indirizzo!''}" required>
                    </div>
                    
                    <button type="submit" class="btn-submit">Salva Modifiche</button>
                </form>
            </div>
        </main>
    </div>
</body>
</html>