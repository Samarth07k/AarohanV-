import { useMutation } from '@tanstack/react-query';
import { mediaUploadService } from '../services/mediaUploadService';

/**
 * Two-step media flow (Blueprint 10.2): request signed URL, PUT the file, then
 * the caller attaches the resulting fileUrl to a post. For the local stub the
 * PUT is a no-op echo, so we resolve to the fileUrl directly.
 */
export function useMediaUpload() {
  return useMutation({
    mutationFn: async (file: File): Promise<{ url: string; width?: number; height?: number }> => {
      const mediaType = file.type.startsWith('video') ? 'VIDEO' : 'IMAGE';
      const signed = await mediaUploadService.requestUploadUrl(mediaType, file.name, file.type);
      // In production: await fetch(signed.uploadUrl, { method: 'PUT', body: file })
      // Local stub: we keep a client-side object URL for preview and use fileUrl as the stored ref.
      const dims = await readImageDims(file).catch(() => ({}));
      return { url: signed.fileUrl, ...dims };
    },
  });
}

function readImageDims(file: File): Promise<{ width?: number; height?: number }> {
  return new Promise((resolve, reject) => {
    if (!file.type.startsWith('image')) return resolve({});
    const img = new Image();
    const url = URL.createObjectURL(file);
    img.onload = () => { resolve({ width: img.width, height: img.height }); URL.revokeObjectURL(url); };
    img.onerror = reject;
    img.src = url;
  });
}
