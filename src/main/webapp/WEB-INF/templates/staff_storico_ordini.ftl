<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Staff - Storico Ordini</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" type="text/css" href="../css/style.css">
</head>
<body class="admin-body">

   <aside class="dash-sidebar">
            <div class="dash-logo" style="margin-top: 10px;">
                <i class="fa-solid fa-utensils"></i> WebDelivery
            </div>
            
            <ul class="dash-nav">
                <li><a href="ordini-live"><i class="fa-solid fa-fire-burner"></i> Ordini Live</a></li>
                <li><a href="storico-ordini" class="active"><i class="fa-solid fa-burger"></i> Storico Ordini</a></li>
                                
                <li style="margin-top: auto;">
                    <a href="../logout" class="danger"><i class="fa-solid fa-arrow-right-from-bracket"></i> Esci</a>
                </li>
            </ul>
        </aside>

    <main class="admin-content">
        <header class="admin-header">
            <h1>Archivio Storico Ordini</h1>
            <div class="user-badge">Operatore: ${utenteLoggato.nomeCompleto}</div>
        </header>

        <section class="admin-section">
            <h2 style="margin-bottom: 20px; color: #2d3748;"><i class="fa-solid fa-box-archive"></i> Tutti gli ordini evasi</h2>
            
            <table class="admin-table">
                <thead>
                    <tr>
                        <th>ID Ordine</th>
                        <th>Orario Consegna</th>
                        <th>Cliente</th>
                        <th>Totale Incassato</th>
                        <th>Stato Finale</th>
                        <th style="text-align: center; width: 150px;">Dettagli</th>
                    </tr>
                </thead>
                <tbody>
                    <#if storicoOrdini?has_content>
                        <#list storicoOrdini as ordine>
                            <tr <#if ordine.stato?lower_case == "annullato">class="row-disabled"</#if>>
                                <td style="font-weight: bold;">#${ordine.id}</td>
                                
                                <td>
                                    <#if ordine.storicoStati?has_content>
                                        <#assign dataConsegna = ordine.storicoStati?sort_by("dataOraModifica")?reverse[0].dataOraModifica>
                                        <i class="fa-solid fa-circle-check" style="color: #48bb78; margin-right: 5px;"></i>
                                        <span style="color: #4a5568; font-weight: 500;">
                                            Il ${dataConsegna?string?substring(0, 10)} alle ${dataConsegna?string?substring(11, 16)}
                                        </span>
                                    <#else>
                                        ${ordine.dataCreazione}
                                    </#if>
                                </td>

                                <td>${ordine.utente.nomeCompleto}</td>
                                <td style="font-weight: 600; color: #116C4A;">€ ${ordine.prezzoTotale}</td>
                                <td>
                                    <span class="status-badge status-${ordine.stato?replace(' ', '-')}">${ordine.stato?upper_case}</span>
                                </td>
                                <td style="text-align: center;">
                                    <a href="dettaglio-ordine?id=${ordine.id}" class="btn-details" style="margin: 0;">
                                        <i class="fa-solid fa-eye"></i> Vedi Scontrino
                                    </a>
                                </td>
                            </tr>
                        </#list>
                    <#else>
                        <tr>
                            <td colspan="6" style="text-align: center; padding: 40px; color: #718096;">
                                <i class="fa-solid fa-folder-open" style="font-size: 2rem; margin-bottom: 10px;"></i><br>
                                L'archivio degli ordini è vuoto.
                            </td>
                        </tr>
                    </#if>
                </tbody>
            </table>
        </section>
    </main>

</body>
</html>