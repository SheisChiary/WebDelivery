<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>WebDelivery - Profilo</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="css/style.css">
    <style>
        body { margin: 0; padding: 0; background-color: #f8f9fa; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }
        .dash-container { display: flex; min-height: 100vh; }
        .dash-sidebar { width: 280px; background-color: #ffffff; border-right: 1px solid #e2e8f0; display: flex; flex-direction: column; padding: 30px 0; }
        .dash-logo { font-size: 1.8rem; color: #116C4A; font-weight: 800; text-align: center; margin-bottom: 40px; }
        .dash-nav { list-style: none; padding: 0; margin: 0; flex-grow: 1; }
        .dash-nav li { margin-bottom: 5px; padding: 0 20px; }
        .dash-nav a { display: flex; align-items: center; gap: 15px; padding: 15px 20px; text-decoration: none; color: #64748b; font-weight: 600; border-radius: 12px; transition: 0.3s; }
        .dash-nav a i { font-size: 1.2rem; width: 20px; text-align: center; }
        .dash-nav a:hover { background-color: #f1f5f9; color: #1a202c; }
        .dash-nav a.active { background-color: #116C4A; color: #ffffff; box-shadow: 0 4px 10px rgba(17, 108, 74, 0.2); }
        .dash-nav a.danger { color: #ef4444; margin-top: auto; }
        .dash-nav a.danger:hover { background-color: #fef2f2; }
        .dash-main { flex-grow: 1; padding: 50px; overflow-y: auto; }
        .dash-header { margin-bottom: 40px; }
        .dash-header h1 { font-size: 2.2rem; color: #1a202c; margin: 0 0 10px 0; font-weight: 800; }
        .dash-header p { color: #64748b; margin: 0; font-size: 1.1rem; }
        .dash-card { background: #ffffff; padding: 40px; border-radius: 20px; box-shadow: 0 10px 30px rgba(0,0,0,0.03); max-width: 600px; }
        .form-row { margin-bottom: 25px; }
        .form-row label { display: block; font-weight: 700; color: #1a202c; margin-bottom: 10px; font-size: 0.95rem; }
        .form-row input { width: 100%; padding: 15px; border: 1px solid #e2e8f0; border-radius: 10px; font-size: 1rem; color: #1a202c; box-sizing: border-box; outline: none; transition: 0.3s; background: #f8fafc; }
        .form-row input:focus { border-color: #116C4A; background: #ffffff; box-shadow: 0 0 0 4px rgba(17, 108, 74, 0.1); }
        .form-row input:disabled { background-color: #f1f5f9; color: #94a3b8; cursor: not-allowed; border-color: transparent; }
        .btn-submit { background-color: #116C4A; color: white; border: none; padding: 16px; border-radius: 10px; font-size: 1.1rem; font-weight: 700; cursor: pointer; width: 100%; transition: 0.3s; margin-top: 10px; }
        .btn-submit:hover { background-color: #064e3b; transform: translateY(-2px); }
        .msg-success { background-color: #ecfdf5; color: #059669; padding: 16px; border-radius: 10px; margin-bottom: 30px; font-weight: 600; display: flex; align-items: center; gap: 12px; border: 1px solid #a7f3d0; }
    </style>
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