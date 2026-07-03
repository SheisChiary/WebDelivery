<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>WebDelivery - I Miei Ordini</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <div class="dash-container">
        <aside class="dash-sidebar">
            <div class="dash-logo"><i class="fa-solid fa-utensils"></i> WebDelivery</div>
            <ul class="dash-nav">
                <li><a href="menu"><i class="fa-solid fa-arrow-left"></i> Torna al Menù</a></li>
                <li><a href="profilo"><i class="fa-solid fa-user"></i> Il mio Profilo</a></li>
                <li><a href="storico-ordini" class="active"><i class="fa-solid fa-clock-rotate-left"></i> I Miei Ordini</a></li>
            </ul>
            <ul class="dash-nav" style="flex-grow: 0;">
                <li><a href="logout" class="danger"><i class="fa-solid fa-right-from-bracket"></i> Esci dall'account</a></li>
            </ul>
        </aside>

        <main class="dash-main">
            <header class="dash-header">
                <h1>I Miei Ordini</h1>
                <p>Monitora lo stato delle tue consegne o consulta lo storico passato.</p>
            </header>

            <div class="order-list">
                <#if ordini?has_content>
                    <#list ordini as ordine>
                        <div class="order-card">
                            <div class="order-top">
                                <div>
                                    <h3 class="order-id">Ordine #${ordine[0]}</h3>
                                    <p class="order-date">Effettuato il ${ordine[1]?string('dd/MM/yyyy HH:mm')}</p>
                                </div>
                                <div class="order-price">€${ordine[3]?string("0.00")}</div>
                            </div>
                            
                            <div class="order-bottom">
                                <div>
                                    <span class="status-label">Stato Attuale</span>
                                    <span class="status-badge 
                                        <#if ordine[2] == 'inserito'>inserito
                                        <#elseif ordine[2] == 'in preparazione'>preparazione
                                        <#elseif ordine[2] == 'pronto'>pronto
                                        <#elseif ordine[2] == 'in consegna'>consegna
                                        <#elseif ordine[2] == 'consegnato'>consegnato
                                        <#elseif ordine[2] == 'annullato'>annullato</#if>">
                                        <#if ordine[2] == 'inserito'><i class="fa-solid fa-file-invoice"></i>
                                        <#elseif ordine[2] == 'in preparazione'><i class="fa-solid fa-fire-burner"></i>
                                        <#elseif ordine[2] == 'pronto'><i class="fa-solid fa-bell-concierge"></i>
                                        <#elseif ordine[2] == 'in consegna'><i class="fa-solid fa-motorcycle"></i>
                                        <#elseif ordine[2] == 'consegnato'><i class="fa-solid fa-check"></i>
                                        <#elseif ordine[2] == 'annullato'><i class="fa-solid fa-xmark"></i></#if>
                                        ${ordine[2]}
                                    </span>
                                </div>
                                
                                <#if ordine[2] != 'consegnato' && ordine[2] != 'annullato'>
                                    <div class="time-box">
                                        <span class="status-label">Tempo Stimato</span>
                                        <div class="time-value"><i class="fa-regular fa-clock" style="color: #116C4A;"></i> ~${ordine[4]} min</div>
                                    </div>
                                </#if>
                            </div>
                        </div>
                    </#list>
                <#else>
                    <div class="empty-state">
                        <i class="fa-solid fa-receipt"></i>
                        <h3>Nessun ordine trovato</h3>
                        <p>Il tuo storico è vuoto. È il momento perfetto per provare qualcosa di nuovo!</p>
                        <a href="menu" class="btn-link">Esplora il Menù</a>
                    </div>
                </#if>
            </div>
        </main>
    </div>
</body>
</html>