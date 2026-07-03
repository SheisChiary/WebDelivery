<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Modifica Staff</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" type="text/css" href="../css/style.css">
</head>
<body class="admin-body">

   <aside class="dash-sidebar">
            <div class="dash-logo" style="margin-top: 10px;">
                <i class="fa-solid fa-utensils"></i> WebDelivery
            </div>
            
            <ul class="dash-nav">
                <li><a href="ordini"><i class="fa-solid fa-receipt"></i> Ordini</a></li>
                <li><a href="menu"><i class="fa-solid fa-burger"></i> Gestione Menù</a></li>
                <li><a href="staff" class="active"><i class="fa-solid fa-users"></i> Gestione Staff</a></li>
                <li><a href="statistiche"><i class="fa-solid fa-chart-line"></i> Statistiche</a></li>
                
                <li style="margin-top: auto;">
                    <a href="../logout" class="danger"><i class="fa-solid fa-arrow-right-from-bracket"></i> Esci</a>
                </li>
            </ul>
        </aside>

    <main class="admin-content">
        <header class="admin-header">
            <h1>
                <a href="staff" style="color: #4a5568; text-decoration: none; margin-right: 15px;">
                    <i class="fa-solid fa-arrow-left"></i>
                </a>
                Modifica Profilo Dipendente
            </h1>
        </header>

        <section class="admin-section" style="max-width: 600px;">
            <form action="staff-modifica" method="POST">
                <input type="hidden" name="id" value="${dipendente.id}">
                
                <div class="form-group" style="margin-bottom: 20px;">
                    <label>Nome Completo</label>
                    <input type="text" name="nome_completo" value="${dipendente.nomeCompleto}" required>
                </div>
                
                <div class="form-group" style="margin-bottom: 20px;">
                    <label>Email</label>
                    <input type="email" name="email" value="${dipendente.email}" required>
                </div>
                
                <div class="form-group" style="margin-bottom: 20px;">
                    <label>Telefono</label>
                    <input type="text" name="telefono" value="${dipendente.telefono!''}">
                </div>
                
                <div class="form-group" style="margin-bottom: 30px;">
                    <label>Nuova Password <small style="font-weight:normal; color:#718096;">(Lascia vuoto per non cambiarla)</small></label>
                    <input type="password" name="password" placeholder="***">
                </div>
                
                <div style="display: flex; gap: 15px;">
                    <button type="submit" class="btn-green">Salva Modifiche</button>
                    <a href="staff" class="btn btn-outline" style="text-align: center; display: inline-block;">Annulla</a>
                </div>
            </form>
        </section>
    </main>

</body>
</html>
