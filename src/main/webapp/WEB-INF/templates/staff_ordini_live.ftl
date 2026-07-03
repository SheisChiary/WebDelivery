<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Staff - Ordini Live</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" type="text/css" href="../css/style.css">
</head>
<body class="admin-body">
   <aside class="dash-sidebar">
            <div class="dash-logo" style="margin-top: 10px;">
                <i class="fa-solid fa-utensils"></i> WebDelivery
            </div>
            
            <ul class="dash-nav">
                <li><a href="ordini-live" class="active"><i class="fa-solid fa-fire-burner"></i> Ordini Live</a></li>
                <li><a href="storico-ordini"><i class="fa-solid fa-burger"></i> Storico Ordini</a></li>
                                
                <li style="margin-top: auto;">
                    <a href="../logout" class="danger"><i class="fa-solid fa-arrow-right-from-bracket"></i> Esci</a>
                </li>
            </ul>
        </aside>

    <main class="admin-content">
        <header class="admin-header">
            <h1>Gestione Ordini Live</h1>
            <div class="user-badge">Operatore: ${utenteLoggato.nomeCompleto}</div>
        </header>

        <section class="admin-section">
            <h2 style="margin-bottom: 20px; color: #2d3748;"><i class="fa-solid fa-clipboard-list"></i> Ordini in Corso</h2>
            
            <table class="admin-table">
                <thead>
                    <tr>
                        <th>ID Ordine</th>
                        <th>Orario Richiesto</th>
                        <th>Cliente</th>
                        <th>Stato Attuale</th>
                        <th style="text-align: center; width: 200px;">Azioni</th>
                    </tr>
                </thead>
                <tbody>
                    <#if ordiniLive?has_content>
                        <#list ordiniLive as ordine>
                            <tr>
                                <td style="font-weight: bold;">#${ordine.id}</td>
                                
                                <td style="font-weight: 500; color: #4a5568;">
                                    <i class="fa-regular fa-calendar" style="color: #718096; margin-right: 5px;"></i>
                                    Prog. per le ${ordine.orarioConsegnaRichiesto?string?substring(11, 16)}
                                </td>
                                
                                <td>${ordine.utente.nomeCompleto}</td>
                                <td>
                                    <span class="status-badge status-${ordine.stato?replace(' ', '-')}">${ordine.stato?upper_case}</span>
                                </td>
                                <td>
                                    <div class="actions-column">
                                        <a href="dettaglio-ordine?id=${ordine.id}" class="btn-details">
                                            <i class="fa-solid fa-eye"></i> Vedi Dettagli
                                        </a>

                                        <#assign nextState = "">
                                        <#assign btnText = "">
                                        <#if ordine.stato?lower_case == "inserito">
                                            <#assign nextState = "in preparazione">
                                            <#assign btnText = "Inizia Preparazione">
                                        <#elseif ordine.stato?lower_case == "in preparazione">
                                            <#assign nextState = "pronto">
                                            <#assign btnText = "Segna come Pronto">
                                        <#elseif ordine.stato?lower_case == "pronto">
                                            <#assign nextState = "in consegna">
                                            <#assign btnText = "Invia in Consegna">
                                        <#elseif ordine.stato?lower_case == "in consegna">
                                            <#assign nextState = "consegnato">
                                            <#assign btnText = "Conferma Consegna">
                                        </#if>

                                        <#if nextState != "">
                                            <form action="ordini-live" method="POST" style="margin: 0;">
                                                <input type="hidden" name="id_ordine" value="${ordine.id}">
                                                <input type="hidden" name="nuovo_stato" value="${nextState}">
                                                <button type="submit" class="btn-action-state">
                                                    <i class="fa-solid fa-arrow-right"></i> ${btnText}
                                                </button>
                                            </form>
                                        </#if>
                                    </div>
                                </td>
                            </tr>
                        </#list>
                    <#else>
                        <tr>
                            <td colspan="5" style="text-align: center; padding: 40px; color: #718096;">
                                <i class="fa-solid fa-check-double" style="font-size: 2rem; margin-bottom: 10px; color: #48bb78;"></i><br>
                                Nessun ordine attivo in questo momento.
                            </td>
                        </tr>
                    </#if>
                </tbody>
            </table>
        </section>
    </main>

</body>
</html>