import React, { createContext, useContext, useEffect, useState } from 'react';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { appColors } from '../theme';

const STORAGE_KEY = 'user_session'; // era PREF_NAME = "user_session"

export function createUser(name) {
  if (!name || name.trim() === '') return null;

  const trimmed = name.trim();
  const isAdmin =
    trimmed.toLowerCase() === 'admin' || trimmed.toLowerCase() === 'adm';

  return {
    name: isAdmin ? 'Admin' : trimmed,
    role: isAdmin ? 'admin' : 'regular',
  };
}

// ─── Context ───────────────────────────────────────────────────────────────────

const SessionContext = createContext(null);

export function SessionProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    (async () => {
      try {
        const json = await AsyncStorage.getItem(STORAGE_KEY);
        if (json) {
          const parsed = JSON.parse(json);
          setUser(parsed);
        }
      } catch (e) {
        console.warn('Erro ao carregar sessão:', e);
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  async function login(name) {
    if (user) return;
    const newUser = createUser(name);
    if (!newUser) return;
    try {
      await AsyncStorage.setItem(STORAGE_KEY, JSON.stringify(newUser));
      setUser(newUser);
    } catch (e) {
      console.warn('Erro ao salvar sessão:', e);
    }
  }

  async function logout() {
    try {
      await AsyncStorage.removeItem(STORAGE_KEY);
      setUser(null);
    } catch (e) {
      console.warn('Erro ao limpar sessão:', e);
    }
  }

  const isLoggedIn = user !== null;
  const isAdmin = user?.role === 'admin';
  const isRegular = user?.role === 'regular';

  return (
    <SessionContext.Provider
      value={{ user, loading, isLoggedIn, isAdmin, isRegular, login, logout }}
    >
      {children}
    </SessionContext.Provider>
  );
}

export function useSession() {
  const ctx = useContext(SessionContext);
  if (!ctx) throw new Error('useSession deve ser usado dentro de <SessionProvider>');
  return ctx;
}