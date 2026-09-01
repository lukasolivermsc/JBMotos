import { getDB } from './db';
import { appColors } from '../theme';

export async function insertMotorcycle({ name, brand, clientUsername }) {
  const db = await getDB();
  await db.executeSql(
    'INSERT INTO motorcycles (name, brand, clientUsername) VALUES (?, ?, ?);',
    [name, brand, clientUsername]
  );
}

export async function getMotorcyclesByUsername(clientUsername) {
  const db = await getDB();
  const [result] = await db.executeSql(
    'SELECT * FROM motorcycles WHERE clientUsername = ?;',
    [clientUsername]
  );
  return rowsToArray(result);
}

export async function getMotorcycleById(id) {
  const db = await getDB();
  const [result] = await db.executeSql(
    'SELECT * FROM motorcycles WHERE id = ?;',
    [id]
  );
  return result.rows.length > 0 ? result.rows.item(0) : null;
}

export async function deleteMotorcycle(id) {
  const db = await getDB();
  // Deleta agendamentos da moto antes de deletar a moto
  await db.executeSql('DELETE FROM schedules WHERE motorcycleId = ?;', [id]);
  await db.executeSql('DELETE FROM motorcycles WHERE id = ?;', [id]);
}

function rowsToArray(result) {
  const arr = [];
  for (let i = 0; i < result.rows.length; i++) {
    arr.push(result.rows.item(i));
  }
  return arr;
}