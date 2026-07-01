<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>WebDelivery - Dashboard</title>
    
    <link rel="stylesheet" type="text/css" href="css/style.css">
</head>
<body>
    
    <aside class="sidebar">
        <h2>WebDelivery</h2>
        <nav>
            <ul>
                <li><a href="home">🏠 Home</a></li>
                <li><a href="menu">🍔 Menù</a></li>
            </ul>
        </nav>
    </aside>

    <main class="content">
        <header>
            <h1>Benvenuta, ${nomeUtente}! 👋</h1>
        </header>
        
        <section class="dashboard-card">
            <p>Il sistema è operativo. Oggi ci sono <strong>${piattiDelGiorno}</strong> ordini in preparazione per il tuo turno.</p>
        </section>
    </main>
    
</body>
</html>