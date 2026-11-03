import { open } from '@op-engineering/op-sqlite';
 
let dbPromise = null;

export function getDB() {
  // Cache the promise so concurrent calls don't open/seed twice
  if (!dbPromise) dbPromise = initDB();
  return dbPromise;
}

async function initDB() {
  const db = open({ name: 'jbmotos.db' });

  await db.execute('PRAGMA foreign_keys = ON;');

  await db.execute(`
    CREATE TABLE IF NOT EXISTS motorcycles (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      name TEXT NOT NULL,
      brand TEXT NOT NULL,
      clientUsername TEXT NOT NULL
    );
  `);

  await db.execute(`
    CREATE TABLE IF NOT EXISTS services (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      name TEXT NOT NULL,
      price INTEGER NOT NULL,
      duration INTEGER NOT NULL,
      description TEXT DEFAULT ''
    );
  `);

  await db.execute(`
    CREATE TABLE IF NOT EXISTS schedules (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      serviceId INTEGER NOT NULL,
      motorcycleId INTEGER NOT NULL,
      timestamp INTEGER NOT NULL,
      FOREIGN KEY (serviceId) REFERENCES services(id) ON DELETE CASCADE,
      FOREIGN KEY (motorcycleId) REFERENCES motorcycles(id) ON DELETE CASCADE
    );
  `);

  // Control table: only marks that the seed has already run
  await db.execute(`
    CREATE TABLE IF NOT EXISTS app_meta (
      key TEXT PRIMARY KEY,
      value TEXT NOT NULL
    );
  `);

  await seedServices(db);

  return db;
}

async function seedServices(db) {
  const result = await db.execute(
    "SELECT value FROM app_meta WHERE key = 'services_seeded';"
  );
  if (result.rows.length > 0) return;

  const services = [
    { name: 'Troca de óleo',               price:  8000, duration:  30, description: 'Troca completa do óleo do motor.' },
    { name: 'Revisão geral',               price: 25000, duration: 120, description: 'Revisão completa da moto.' },
    { name: 'Alinhamento e balanceamento', price: 12000, duration:  60, description: 'Alinhamento e balanceamento das rodas.' },
    { name: 'Troca de pneu dianteiro',     price: 15000, duration:  45, description: 'Troca do pneu dianteiro.' },
    { name: 'Troca de pneu traseiro',      price: 17000, duration:  45, description: 'Troca do pneu traseiro.' },
    { name: 'Troca de freios',             price: 20000, duration:  60, description: 'Substituição das pastilhas e discos de freio.' },
    { name: 'Limpeza de carburador',       price:  9000, duration:  40, description: 'Limpeza e ajuste do carburador.' },
    { name: 'Troca de corrente',           price: 11000, duration:  30, description: 'Substituição da corrente de transmissão.' },
    { name: 'Regulagem de válvulas',       price: 14000, duration:  90, description: 'Regulagem e ajuste das válvulas do motor.' },
    { name: 'Diagnóstico eletrônico',      price:  5000, duration:  20, description: 'Leitura de erros e diagnóstico via scanner.' },
  ];

  // Single transaction: all inserts + the meta flag succeed or none do
  await db.transaction(async (tx) => {
    for (const s of services) {
      await tx.execute(
        'INSERT INTO services (name, price, duration, description) VALUES (?, ?, ?, ?);',
        [s.name, s.price, s.duration, s.description]
      );
    }
    await tx.execute(
      "INSERT INTO app_meta (key, value) VALUES ('services_seeded', '1');"
    );
  });
}