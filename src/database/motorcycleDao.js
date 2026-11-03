import { getDB } from './db';

export async function insertMotorcycle({ name, brand, clientUsername }) {
  const db = await getDB();
  await db.execute(
    'INSERT INTO motorcycles (name, brand, clientUsername) VALUES (?, ?, ?);',
    [name, brand, clientUsername]
  );
}

export async function getMotorcyclesByUsername(clientUsername) {
  const db = await getDB();
  const result = await db.execute(
    'SELECT * FROM motorcycles WHERE clientUsername = ?;',
    [clientUsername]
  );
  return result.rows;
}

export async function getMotorcycleById(id) {
  const db = await getDB();
  const result = await db.execute(
    'SELECT * FROM motorcycles WHERE id = ?;',
    [id]
  );
  return result.rows.length > 0 ? result.rows[0] : null;
}

export async function deleteMotorcycle(id) {
  const db = await getDB();
  // Deleta agendamentos da moto antes de deletar a moto
  await db.execute('DELETE FROM schedules WHERE motorcycleId = ?;', [id]);
  await db.execute('DELETE FROM motorcycles WHERE id = ?;', [id]);
}