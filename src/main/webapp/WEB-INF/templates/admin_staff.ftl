<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Dashboard Admin - Gestione Staff</title>
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
            <h1>Gestione Staff</h1>
            <div class="user-badge">Proprietario: ${utenteLoggato.nomeCompleto}</div>
        </header>

        <section class="admin-section">
            <h2><i class="fa-solid fa-user-plus"></i> Aggiungi nuovo membro</h2>
            
            <form action="staff" method="POST">
                <div class="form-grid">
                    <div class="form-group">
                        <label for="nome_completo">Nome Completo</label>
                        <input type="text" id="nome_completo" name="nome_completo" required placeholder="Es. Mario Rossi">
                    </div>
                    <div class="form-group">
                        <label for="email">Email</label>
                        <input type="email" id="email" name="email" required placeholder="mario@webdelivery.it">
                    </div>
                    <div class="form-group">
                        <label for="telefono">Numero di Telefono</label>
                        <input type="text" id="telefono" name="telefono" placeholder="Es. 333 1234567">
                    </div>
                    <div class="form-group">
                        <label for="password">Password (Provvisoria)</label>
                        <input type="password" id="password" name="password" required>
                    </div>
                </div>
                <button type="submit" class="btn-green">Conferma</button>
            </form>
        </section>

        <section class="admin-section" style="margin-top: 40px;">
            <h2><i class="fa-solid fa-users-gear"></i> Membri Attuali</h2>
            <table class="admin-table">
                <thead>
                    <tr>
                        <th>Nome Completo</th>
                        <th>Email</th>
                        <th>Telefono</th>
                        <th style="text-align: center;">Azioni</th>
                    </tr>
                </thead>
                <tbody>
                    <#if staffList?has_content>
                        <#list staffList as membro>
                            <tr>
                                <td>${membro.nomeCompleto}</td>
                                <td>${membro.email}</td>
                                <td>${membro.telefono!'-'}</td>
                                <td style="text-align: center;">
                                    <a href="staff-modifica?id=${membro.id}" class="action-icon edit" title="Modifica">
                                        <i class="fa-solid fa-pen"></i>
                                    </a>
                                    <form action="staff-elimina" method="POST" style="display:inline;" onsubmit="return confirm('Sei sicuro di voler rimuovere ${membro.nomeCompleto}?');">
                                        <input type="hidden" name="id" value="${membro.id}">
                                        <button type="submit" style="background:none; border:none; padding:0;" class="action-icon delete" title="Rimuovi">
                                            <i class="fa-solid fa-trash"></i>
                                        </button>
                                    </form>
                                </td>
                            </tr>
                        </#list>
                    <#else>
                        <tr><td colspan="4" style="text-align: center;">Nessun membro dello staff registrato.</td></tr>
                    </#if>
                </tbody>
            </table>
        </section>
    </main>

</body>
</html>
